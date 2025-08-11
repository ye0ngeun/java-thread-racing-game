package dev.racing;

/**
 * 경주마를 나타내는 스레드 클래스
 * 각 말은 독립적인 스레드로 실행되며, 무작위 속도로 경주를 진행한다.
 * 
 * 주요 기능:
 * - 무작위 속도로 경주 진행
 * - 스레드 안전성을 위한 동기화 처리
 * - 도착 시 결과 자동 기록
 * 
 */
public class Horse extends Thread {
    /** 말의 고유 번호 */
    private final int horseNumber;
    
    /** 현재 말의 진행 거리 (0~50) */
    private int currentDistance = 0;
    
    /** 경주 결과를 기록하는 객체 */
    private final RaceResult resultRecorder;

    /**
     * Horse 생성자
     * 
     * @param horseNumber 말의 고유 번호
     * @param resultRecorder 경주 결과를 기록할 RaceResult 객체
     */
    public Horse(int horseNumber, RaceResult resultRecorder) {
        this.horseNumber = horseNumber;
        this.resultRecorder = resultRecorder;
    }

    /**
     * 말의 번호를 반환하는 getter 메소드
     * 
     * @return 말의 고유 번호
     */
    public int getNumber() {
        return horseNumber;
    }

    /**
     * 현재 말의 진행 거리를 반환하는 동기화된 getter 메소드
     * 스레드 안전성을 보장하기 위해 synchronized 키워드 사용
     * 
     * @return 현재 진행 거리 (0~50)
     */
    public synchronized int getDistance() {
        return currentDistance;
    }

    /**
     * 스레드 실행 메소드
     * 말이 경주를 시작하고 도착선(거리 50)에 도달할 때까지 달린다.
     * 무작위 속도로 진행하며, 도착 시 결과를 자동으로 기록한다.
     */
    @Override
    public void run() {
        // 도착선(50)에 도달할 때까지 달리기
        while (currentDistance < 50) {
            // 0~2 사이의 무작위 거리만큼 전진
            currentDistance += (int)(Math.random() * 3);
            
            // 거리가 50을 초과하면 정확히 50으로 설정 (도착선 정확히 맞추기)
            if (currentDistance > 50) currentDistance = 50;

            try {
                Thread.sleep(200); // 말마다 달리는 속도 (200ms 간격)
            } catch (InterruptedException interruptedException) {
                break; // 인터럽트 발생 시 경주 중단
            }
        }
        
        // 도착 시 결과 기록 (말 번호와 함께)
        resultRecorder.recordFinish("🐎 " + horseNumber + "번 말");
    }
}