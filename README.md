# java-thread-racing-game

말 한 마리를 스레드 하나로 만들어 동시에 달리게 하는 콘솔 경마 게임입니다.
여러 스레드가 같은 데이터를 읽고 쓸 때 생기는 문제를 직접 겪고 고쳐 보려고 만들었습니다.


## 동시성에서 신경 쓴 것

- **도착 순서 기록**: 여러 말이 거의 동시에 들어와도 순위가 꼬이지 않게 `RaceResult.recordFinish`를 `synchronized`로 묶었습니다.
- **거리 읽기**: 화면 스레드가 말의 위치를 읽는 동안 말 스레드가 값을 바꾸므로 `Horse.getDistance`도 `synchronized`로 읽습니다.
- **목록 순회 중 수정 예외**: 화면 스레드가 말 목록을 도는 중에 목록이 바뀌어 `ConcurrentModificationException`이 났습니다. 화면 스레드에는 수정할 수 없는 목록을 넘겨 해결했습니다.
- **종료 순서**: 메인 스레드는 모든 말 스레드를 `join`으로 기다린 뒤 화면 스레드를 `interrupt`로 멈추고 결과를 출력합니다. 인터럽트를 받으면 상태를 복원해 끝냅니다.

## 구성

| 클래스 | 역할 |
|---|---|
| `RaceController` | 말 생성, 경주 시작, 완주 대기, 결과 출력 |
| `Horse` | 말 한 마리, 스레드 |
| `DisplayThread` | 트랙을 주기적으로 그리는 스레드 |
| `RaceResult` | 도착 순서 기록과 순위 출력 |

## 기술

Java, Thread, SLF4J, Logback
