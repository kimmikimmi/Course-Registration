import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MyConcurrentQ {
    //private MyNode[] myNodes;
    private String[] myNodes;
    private AtomicReferenceArray nodes;
    private int capacity;

    private AtomicInteger front;
    private AtomicInteger rear;

    // 순환 큐 생성
    public MyConcurrentQ(int capacity) {
        // 초기화
        this.capacity = capacity;
        front= new AtomicInteger(0);
        rear = new AtomicInteger(0);

        // 배열 생성 & 메모리 할당
//        myNodes = new MyNode[capacity + 1];
//        for (int i = 0; i < capacity + 1; i++) {
//            myNodes[i] = new MyNode();
//        }
//        nodes = new AtomicReferenceArray(myNodes);
        myNodes = new String[capacity + 1];
        for (int i = 0; i < capacity + 1; i++) {
            myNodes[i] = new String();
        }
        nodes = new AtomicReferenceArray(myNodes);

    }

    public String getElement() {
        System.out.println("front.get() = " + front.get());
        //return ((MyNode)(nodes.get(front.get()))).getData();
        return nodes.get(front.get()).toString();
    }

    // 삽입
    public boolean enqueue(String data) {
        AtomicInteger position = new AtomicInteger();
        position.set(0);

        // 큐의 후방(rear)이 배열을 벗어났다면
        if (rear.get() == capacity + 1) {
            // 후방의 index를 0으로 초기화(순환 큐이므로 계속 돌아온다.)
            rear.set(0);
            position.set(0);
        } else
            // 그렇지 않다면 그대로 증가
            position.set(rear.getAndIncrement());

        // 데이터 삽입
        //myNodes[position.get()] = data;

        nodes.set(position.get(), data);
        System.out.println("value which went into currently is " + nodes.get(position.get()) + " size of Q = " + getSize());
        return true;

    }

    // 제거
    public Object dequeue() {
        AtomicInteger position = new AtomicInteger();
        position.set(front.get());

        // 큐의 전방(front)이 배열 끝에 위치해있으면
        if (front.get() == capacity)
            front.set(0);
        else
            // 그렇지 않다면 그대로 증가
            front.incrementAndGet();

        //return myNodes[position].getData();
        //return ( (MyNode)(nodes.get( position.get() ) ) ).getData();
        System.out.println("value which came out current ly is " + nodes.get(position.get()).toString() + getSize());
        return nodes.get(position.get()).toString();
    }

    // 사이즈 반환
    public int getSize() {
        // 전방 index가 후방 index보다 앞에 위치해 있다면
        if (front.get() <= rear.get())
            return rear.get() - front.get();
        else
            return (capacity + 1) - front.get() + rear.get();
    }

    // 완전히 비어있는지
    public boolean isEmpty() {
        return front.get() == rear.get();
    }

    // 꽉 차 있는지
    public boolean isFull() {
        // 전방 index가 후방 index보다 앞에 위치해 있다면
        if (front.get() < rear.get())
            //return (rear - front) == capacity;
            return (rear.get()-front.get()) == capacity;
        else
            //return (rear + 1) == front;
        return (rear.get() + 1) == front.get();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFront() {
        return front.get();
    }

    public int getRear() {
        return rear.get();
    }


}
