package dev.racing;

import java.util.*;

/**
 * 경주 진행 상황을 실시간으로 모니터링하고 화면에 출력하는 스레드 클래스
 * 말 번호순으로 트랙을 정렬하여 표시하며, 부드러운 애니메이션 효과를 제공한다.
 * 
 * 주요 기능:
 * - 실시간 경주 상황 모니터링
 * - 콘솔 화면 깜빡임 방지
 * - 경주 완료 상태 체크
 * - 최종 결과 대기 시간 제공
 * 
 */
public class DisplayThread extends Thread {
    /** 경주에 참가하는 모든 말들의 리스트 */
    private final List<Horse> raceHorses;
    
    /** 첫 번째 화면 출력 여부를 확인하는 플래그 (커서 위치 제어용) */
    private boolean isInitialDisplay = true;

    /**
     * DisplayThread 생성자
     * 
     * @param raceHorses 경주에 참가하는 말들의 리스트 (불변 리스트 권장)
     */
    public DisplayThread(List<Horse> raceHorses) {
        this.raceHorses = raceHorses;
    }

    /**
     * 스레드 실행 메소드
     * 경주가 완료될 때까지 실시간으로 경주 상황을 화면에 출력한다.
     */
    @Override
    public void run() {
        // 경주가 진행 중인 동안 계속 실행 (인터럽트될 때까지)
        while (!isInterrupted()) {
            // 첫 출력이 아니라면 커서를 위로 올려서 덮어쓰기 (화면 깜빡임 방지)
            if (!isInitialDisplay) {
                // 헤더 1줄 + 말 개수 + 하단 메시지 1줄 = 총 라인 수
                int totalDisplayLines = 1 + raceHorses.size() + 1;
                System.out.print("\033[" + totalDisplayLines + "A"); // ANSI 이스케이프 코드로 커서를 위로 올림
                //콘솔 갱신 반짝임 해결
            }
            
            // 경주 상황 헤더 출력
            System.out.println("📢 현재 경주 상황:");
            
            // 말 번호순 정렬하여 출력
            // 기존엔 unmodifiableList를 사용한 리스트를 정렬하려 해서 오류
            // 스트림은 데이터 소스로 부터 데이터를 읽기만할 뿐, 데이터 소스를 변경하지 않는다.
            raceHorses.stream()
                .sorted(Comparator.comparingInt(Horse::getNumber)) // 말 번호 기준 오름차순 정렬
                .forEach(currentHorse -> System.out.printf("%2d번 말: %s%n", 
                    currentHorse.getNumber(), 
                    UIPrinter.buildTrack(currentHorse.getDistance())));

            // 모든 말이 도착했는지 체크
            if (isRaceCompleted()) {
                System.out.println("🏁 모든 말이 도착했습니다!");
                try {
                    Thread.sleep(10000); // 마지막 상태를 10초간 보여줌 이로써 마지막 말 낙오 해결
                } catch (InterruptedException interruptedException) {
                    // 인터럽트되면 바로 종료
                }
                break; // 경주 완료 시 루프 종료
            }

            // 진행 상태 안내 메시지
            System.out.println("(트랙 출력 중... 최종 순위는 도착 후 출력됩니다)");
            
            isInitialDisplay = false; // 첫 출력 완료 표시

            try {
                Thread.sleep(300); // 300ms 간격으로 화면 갱신 (부드러운 애니메이션)
            } catch (InterruptedException interruptedException) {
                break; // 인터럽트 발생 시 스레드 종료
            }
        }
    }

    /**
     * 모든 말이 경주를 완료했는지 확인하는 메소드
     * 
     * @return 모든 말이 50 이상의 거리에 도달했으면 true, 그렇지 않으면 false
     */
    private boolean isRaceCompleted() {
        return raceHorses.stream().allMatch(horse -> horse.getDistance() >= 50);
    }
}