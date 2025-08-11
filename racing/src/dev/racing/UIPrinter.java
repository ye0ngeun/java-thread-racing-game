package dev.racing;

/**
 * 사용자 인터페이스 출력을 담당하는 유틸리티 클래스
 * 경주 트랙 생성 및 콘솔 화면 제어 기능을 제공한다.
 * 
 * 주요 기능:
 * - 말의 위치에 따른 트랙 시각화
 * - 콘솔 화면 클리어 기능
 * 
 */
public class UIPrinter {
    
    /**
     * 말의 현재 위치를 시각적으로 표현하는 트랙을 생성하는 메소드
     * 
     * @param currentDistance 말의 현재 위치 (0~50)
     * @return 말의 위치가 표시된 트랙 문자열 (말🐎 + 트랙- + 도착선🏁)
     */
    public static String buildTrack(int currentDistance) {
        StringBuilder trackBuilder = new StringBuilder();
        int finishLinePosition = 50; // 도착 지점

        // 트랙 구간별로 문자 배치
        for (int trackPosition = 0; trackPosition <= finishLinePosition; trackPosition++) {
            if (trackPosition == currentDistance) {
                trackBuilder.append("🐎"); // 말의 현재 위치
            } else {
                trackBuilder.append("-"); // 빈 트랙 구간
            }
        }
        trackBuilder.append("🏁"); // 도착선 표시
        return trackBuilder.toString();
    }
    
    /**
     * ANSI 이스케이프 코드를 사용하여 콘솔 화면을 클리어하는 메소드
     * 화면을 깨끗하게 지우고 커서를 맨 위로 이동시킨다.
     */
    // ANSI escape code로 콘솔 클리어
    public static void clearConsole() {
        System.out.print("\033[H\033[2J"); // H: 커서를 홈 위치로, 2J: 전체 화면 지우기
        System.out.flush(); // 출력 버퍼 강제 플러시
    }
}