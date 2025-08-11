package dev.racing;

import java.util.ArrayList;
import java.util.List;

/**
 * 경주 결과를 관리하는 클래스
 * 말들의 도착 순서를 기록하고 최종 순위를 출력하는 기능을 제공한다.
 * 
 * 주요 기능:
 * - 도착 순서별 말 정보 기록
 * - 스레드 안전한 결과 기록
 * - 최종 순위 출력
 * 
 */
public class RaceResult {
    /** 도착 순서대로 말들의 정보를 저장하는 리스트 */
    private final List<String> finishOrderList = new ArrayList<>();

    /**
     * 말의 도착을 기록하는 동기화된 메소드
     * 여러 말이 동시에 도착할 때 순서를 정확하게 기록하기 위해 synchronized 사용
     * 
     * @param horseIdentifier 도착한 말의 식별 정보 (번호와 이모지 포함)
     */
    public synchronized void recordFinish(String horseIdentifier) {
        finishOrderList.add(horseIdentifier);
    }

    /**
     * 최종 경주 결과를 순위별로 출력하는 메소드
     * 도착 순서대로 1등부터 꼴등까지 순위를 표시한다.
     */
    public void printRanking() {
        for (int rankPosition = 0; rankPosition < finishOrderList.size(); rankPosition++) {
            System.out.printf("%2d등: %s\n", rankPosition + 1, finishOrderList.get(rankPosition));
        }
    }
}