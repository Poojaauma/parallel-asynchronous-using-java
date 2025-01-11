package com.learnjava.parallelstream;

import com.learnjava.util.DataSet;

import java.util.List;
import java.util.stream.Stream;

import static com.learnjava.util.CommonUtil.*;
import static com.learnjava.util.LoggerUtil.log;

public class ParallelStreamExample {
    public List<String> stringTransform(List<String> nameList) {
        return nameList.parallelStream().map(this::addNameLengthTransform).toList();
    }

    public List<String> stringTransformToLowerCase(List<String> nameList) {
        return nameList.parallelStream().map(this::lowercaseTransform).toList();
    }

    //Dynamically switch between seq and parallel
    public List<String> stringTransform_1(List<String> nameList, boolean isParallel) {
        Stream<String> nameStream = nameList.stream();
        if(isParallel) {
            nameStream = nameStream.parallel();
        }
        return nameStream.map(this::addNameLengthTransform).toList();
    }
    public static void main(String[] args) {
        List<String> nameList = DataSet.namesList();
        ParallelStreamExample parallelStreamExample = new ParallelStreamExample();
        startTimer();
        List<String> resList = parallelStreamExample.stringTransform(nameList);
        log("res : " + resList);
        timeTaken();
    }

    private String addNameLengthTransform(String name) {
        delay(500);
        return name.length()+" - "+name ;
    }

    private String lowercaseTransform(String name) {
        delay(500);
        return name.toLowerCase() ;
    }
}
