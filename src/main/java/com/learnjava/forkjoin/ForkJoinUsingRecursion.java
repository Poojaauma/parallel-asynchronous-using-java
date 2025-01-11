package com.learnjava.forkjoin;

import com.learnjava.util.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

public class ForkJoinUsingRecursion extends RecursiveTask<List<String>> {

    private List<String> inputList;

    public ForkJoinUsingRecursion(List<String> inputList) {
        this.inputList = inputList;
    }

    public static void main(String[] args) {

        stopWatch.start();
        List<String> resultList = new ArrayList<>();
        List<String> names = DataSet.namesList();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinUsingRecursion fjr = new ForkJoinUsingRecursion(names);
        resultList = forkJoinPool.invoke(fjr);// Tasks will be added to shared queue

        stopWatch.stop();
        log("Final Result : "+ resultList);
        log("Total Time Taken : "+ stopWatch.getTime());
    }


    private static String addNameLengthTransform(String name) {
        delay(500);
        return name.length()+" - "+name ;
    }

    @Override
    protected List<String> compute() {
        if(inputList.size() <= 1) {
            List<String> resultList = new ArrayList<>();
            inputList.forEach((name)->resultList.add(addNameLengthTransform(name)));
            return resultList;
        }
        int mid = inputList.size()/2;
        ForkJoinTask<List<String>> leftInput =
                new ForkJoinUsingRecursion(inputList.subList(0, mid)).fork();
        inputList = inputList.subList(mid, inputList.size());
        List<String> rightRes = compute();//recursion
        List<String> leftRes = leftInput.join();
        leftRes.addAll(rightRes);
        return leftRes;
    }
}
