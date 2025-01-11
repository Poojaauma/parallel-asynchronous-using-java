package com.learnjava.parallelstream;

import com.learnjava.util.DataSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParallelStreamExampleTest {
    ParallelStreamExample parallelStreamExample = new ParallelStreamExample();

    @Test
     public void stringTransform() {
        //given
        List<String> inputList = DataSet.namesList();

        //when
        List<String> outputList = parallelStreamExample.stringTransform(inputList);

        //then
        assertEquals(inputList.size(), outputList.size());
        outputList.forEach(name -> {
            assertTrue(name.contains("-"));
        });
    }

    @Test
    public void stringTransformToLowerCase() {
        //given
        List<String> inputList = DataSet.namesList();

        //when
        List<String> outputList = parallelStreamExample.stringTransformToLowerCase(inputList);

        //then
        assertEquals(inputList.size(), outputList.size());
        outputList.forEach(name -> {
            assertTrue(name.equals(name.toLowerCase()));
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void stringTransform_1(boolean isParallel) {
        //given
        List<String> inputList = DataSet.namesList();

        //when
        List<String> outputList = parallelStreamExample.stringTransform_1(inputList, isParallel);

        //then
        assertEquals(inputList.size(), outputList.size());
        outputList.forEach(name -> {
            assertTrue(name.contains("-"));
        });
    }

}