package dev.racing;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 경마 게임의 메인 실행 클래스
 * 사용자로부터 입력을 받아 경주를 시작하는 진입점 역할을 한다.
 * 
 * 실행 순서:
 * 1. 사용자에게 경주 참가 말 수 입력 요청
 * 2. RaceController 생성 및 경주 시작
 * 
 */
public class Main {
    
    /** 로거 인스턴스 */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * 프로그램의 메인 메소드
     * 경마 게임을 시작하고 사용자 입력을 처리한다.
     * 
     * @param args 명령행 인수 (사용하지 않음)
     * @throws InterruptedException 스레드 대기 중 인터럽트 발생 시
     */
    public static void main(String[] args) throws InterruptedException {
        logger.info("=== 경마 게임 시작 ===");
        
        Scanner userInputScanner = new Scanner(System.in);

        // 게임 시작 안내 메시지
        System.out.println("🐎 경마 게임에 오신 걸 환영합니다!");
        System.out.print("경주에 참가할 말의 수를 입력하세요: ");
        
        // 사용자로부터 참가 말 수 입력 받기
        int numberOfHorses = userInputScanner.nextInt();
        logger.info("사용자가 입력한 말의 수: {}", numberOfHorses);
        
        // 입력값 유효성 검사
        if (numberOfHorses <= 0) {
            logger.error("잘못된 말의 수 입력: {}", numberOfHorses);
            System.out.println("말의 수는 1 이상이어야 합니다.");
            return;
        }
        
        if (numberOfHorses > 20) {
            logger.warn("많은 수의 말 입력: {}. 성능에 영향을 줄 수 있습니다.", numberOfHorses);
        }

        try {
            // 경주 컨트롤러 생성 및 경주 시작
            logger.debug("RaceController 생성 중... 말의 수: {}", numberOfHorses);
            RaceController gameController = new RaceController(numberOfHorses);
            
            logger.info("경주 시작!");
            long startTime = System.currentTimeMillis();
            
            gameController.startRace();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            logger.info("경주 완료! 총 소요시간: {}ms", duration);
            
        } catch (InterruptedException e) {
            logger.error("경주 진행 중 인터럽트 발생", e);
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            throw e;
        } catch (Exception e) {
            logger.error("경주 진행 중 예외 발생", e);
            System.out.println("경주 진행 중 오류가 발생했습니다: " + e.getMessage());
        } finally {
            logger.info("=== 경마 게임 종료 ===");
            userInputScanner.close();
        }
    }
}