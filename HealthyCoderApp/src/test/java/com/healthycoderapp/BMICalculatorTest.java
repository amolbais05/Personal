package com.healthycoderapp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class BMICalculatorTest {

    private String environment = "prod";

    @BeforeAll
    static void beforeAll(){
        System.out.println("Before all unit test.");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("After all unit test.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {89.0, 95.0, 110.0})
    void should_ReturnTrue_When_DietRecommended(Double coderWeight){

        // given
        double height = 1.72;
        double weight = coderWeight;

        // when
        boolean recommended = BMICalculator.isDietRecommended(weight,height);

        // then
        assertTrue(recommended);
    }

    @ParameterizedTest(name = "weight={0}, height={1}")
    @CsvSource(value = {"89.0, 1.72", "95.0, 1.75", "110.0, 1.78"})
    void should_ReturnTrue_When_DietRecommended_CsvSource(Double coderWeight, Double coderHeight){

        // given
        double height = coderHeight;
        double weight = coderWeight;

        // when
        boolean recommended = BMICalculator.isDietRecommended(weight,height);

        // then
        assertTrue(recommended);
    }

    @ParameterizedTest(name = "weight={0}, height={1}")
    @CsvFileSource(resources = "/diet-recommended-input-data.csv", numLinesToSkip = 1)
    void should_ReturnTrue_When_DietRecommended_CsvFileSource(Double coderWeight, Double coderHeight){

        // given
        double height = coderHeight;
        double weight = coderWeight;

        // when
        boolean recommended = BMICalculator.isDietRecommended(weight,height);

        // then
        assertTrue(recommended);
    }



    @Test
    void should_ReturnFalse_When_DietRecommended(){

        // given
        double height = 50.0;
        double weight = 1.92;

        // when
        boolean recommended = BMICalculator.isDietRecommended(weight,height);

        // then
        assertFalse(recommended);
    }

    @Test
    void should_ThrowArithmeticException_When_HeightZero(){

        // given
        double height = 0.0;
        double weight = 1.92;

        // when
        Executable executable = () -> BMICalculator.isDietRecommended(weight,height);

        // then
        assertThrows(ArithmeticException.class,executable);
    }

    @Test
    void should_ReturnCoderWithWorstBMI_When_CoderListNonEmpty(){

        // given
        List<Coder> coders = new ArrayList<>();
        coders.add(new Coder(1.80,60.0));
        coders.add(new Coder(1.82,98.0));
        coders.add(new Coder(1.82,64.7));

        // when
        Coder codersWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

        //then
        assertAll(
                () -> assertEquals(1.82,codersWorstBMI.getHeight()),
                () -> assertEquals(98.0,codersWorstBMI.getWeight())
        );
    }

    @Test
    void should_ReturnNullWorstBMICoder_When_CoderListEmpty(){

        // given
        List<Coder> coders = new ArrayList<>();

        // when
        Coder codersWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

        //then
        assertNull(codersWorstBMI);
    }

    @Test
    void should_ReturnCorrectBMIScoreArray_When_CoderListNonEmpty(){

        // given
        List<Coder> coders = new ArrayList<>();
        coders.add(new Coder(1.80,60.0));
        coders.add(new Coder(1.82,98.0));
        coders.add(new Coder(1.82,64.7));
        double[] expected = {18.52, 29.59, 19.53};

        // when
        double[] bmiScores = BMICalculator.getBMIScores(coders);

        //then
        assertArrayEquals(expected, bmiScores);

    }

    @Test
    void should_ReturnCoderWithWorstBMIIn1Ms_When_CoderListHas10000Elements(){

        // given
        assumeTrue(this.environment.equals("prod"));
        List<Coder> coders = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            coders.add(new Coder(1.0+i, 10.0+i));
        }

        // when
        Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);

        // then
        assertTimeout(Duration.ofMillis(50), executable);

    }
}