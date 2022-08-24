package org.example.main;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        int a = 0;
        a = a++;
        System.out.println(a);
    }
}

@Entity
class Test {

    @Id
    Integer id;

}

@Component
@ComponentScan
@RequiredArgsConstructor
class Test2 {
    @Id
    Integer id;

    @OneToMany
    final Test test;
}


