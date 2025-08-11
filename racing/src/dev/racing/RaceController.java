package dev.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 경마 게임의 전체 진행을 관리하는 컨트롤러 클래스
 * 말 생성, 경주 시작, 결과 출력 등 게임의 모든 흐름을 총괄한다.
 * 
 * 주요 기능:
 * - 지정된 수만큼 경주마 생성
 * - 경주 진행 및 모니터링 스레드 관리
 * - 경주 완료 후 최종 결과 출력
 * - 스레드 간 동기화 처리
 * 
 */
//게임 진행 총괄 클래스 
public class RaceController {
    /** 로거 인스턴스 */
    private static final Logger logger = LoggerFactory.getLogger(RaceController.class);
    
    /** 경주에 참가할 말의 총 개수 */
    private final int totalHorseCount;
    
    /** 경주에 참가하는 모든 말들의 리스트 */
    private final List<Horse> participatingHorses = new ArrayList<>();
    
    /** 경주 결과를 기록하고 관리하는 객체 */
    private final RaceResult raceResultManager = new RaceResult();

    /**
     * RaceController 생성자
     * 지정된 수만큼 경주마를 생성하여 리스트에 추가한다.
     * 
     * @param totalHorseCount 경주에 참가할 말의 개수
     */
    public RaceController(int totalHorseCount) {
        this.totalHorseCount = totalHorseCount;
        logger.info("RaceController 생성 - 참가 말 수: {}", totalHorseCount);

        // 말 생성 (1번부터 시작)
        for (int horseIndex = 1; horseIndex <= totalHorseCount; horseIndex++) {
            Horse newHorse = new Horse(horseIndex, raceResultManager);
            newHorse.setName("Horse-" + horseIndex); // 스레드 이름 설정
            participatingHorses.add(newHorse);
            logger.debug("{}번 말 생성 및 리스트 추가 완료", horseIndex);
        }
        
        logger.info("모든 말 생성 완료. 총 {}마리", participatingHorses.size());
    }

    /**
     * 경주를 시작하고 완료까지 관리하는 메소드
     * 
     * 실행 순서:
     * 1. 화면 출력 스레드 시작
     * 2. 모든 말 스레드 시작
     * 3. 모든 말의 경주 완료 대기
     * 4. 화면 출력 스레드 종료
     * 5. 최종 결과 출력
     * 
     * @throws InterruptedException 스레드 대기 중 인터럽트 발생 시
     */
    public void startRace() throws InterruptedException {
        logger.info("======= 경주 시작 =======");
        
        // 트랙 출력 스레드 시작
        // 불변리스트로 전달하여 ConcurrentModificationException 방지
        // 기존 방식에선 Exception in thread "main" java.util.ConcurrentModificationException 예외
        // ArrayList를 하나의 스레드에서 순회 중(for-each 또는 .forEach()) 동시에 다른 스레드에서 구조를 수정(add/remove) 하면 발생
        DisplayThread raceMonitorThread = new DisplayThread(Collections.unmodifiableList(participatingHorses)); 
        raceMonitorThread.setName("RaceMonitor-Thread"); // 스레드 이름 설정
        logger.debug("DisplayThread 생성 및 시작");
        raceMonitorThread.start();

        // 잠깐 대기 (DisplayThread가 먼저 시작하도록)
        Thread.sleep(100);

        // 모든 말 스레드 시작 (경주 개시)
        logger.info("모든 말 스레드 시작");
        for (Horse racingHorse : participatingHorses) {
            racingHorse.start();
            logger.debug("{}번 말 스레드 시작", racingHorse.getNumber());
        }
        
        logger.info("모든 말이 경주를 시작했습니다!");

        // 모든 말의 경주 완료 대기 (join으로 스레드 완료 기다림)
        logger.debug("모든 말의 경주 완료 대기 시작");
        for (Horse racingHorse : participatingHorses) {
            logger.trace("{}번 말 완주 대기 중...", racingHorse.getNumber());
            racingHorse.join(); // 이 지점에서 메인 스레드가 WAITING 상태가 됨
            logger.debug("{}번 말 완주 확인", racingHorse.getNumber());
        }
        
        logger.info("모든 말이 완주했습니다!");

        // 화면 출력 스레드 종료 (경주 완료 후)
        logger.debug("DisplayThread 종료 신호 전송");
        raceMonitorThread.interrupt();
        
        // DisplayThread 종료 대기
        try {
            raceMonitorThread.join(5000); // 최대 5초 대기
            if (raceMonitorThread.isAlive()) {
                logger.warn("DisplayThread가 5초 내에 종료되지 않음");
            } else {
                logger.debug("DisplayThread 정상 종료 확인");
            }
        } catch (InterruptedException e) {
            logger.error("DisplayThread 종료 대기 중 인터럽트", e);
            Thread.currentThread().interrupt();
            throw e;
        }

        // 최종 결과 출력
        logger.info("======= 최종 결과 출력 =======");
        System.out.println("\n🏁 최종 도착 순위");
        raceResultManager.printRanking();
        
        logger.info("======= 경주 완료 =======");
    }
}