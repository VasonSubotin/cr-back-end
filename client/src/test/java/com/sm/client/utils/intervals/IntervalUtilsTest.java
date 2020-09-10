package com.sm.client.utils.intervals;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class IntervalUtilsTest {

    @Test
    public void testSubstruct() {
        List<Interval> intervalA = Arrays.asList(
                new Interval(1_000, 10_000, "a1"),
                new Interval(10_000, 20_000, "a2"),
                new Interval(21_000, 30_000, "a3"),
                new Interval(31_000, 40_000, "a4"),
                new Interval(60_000, 80_000, "a5"),
                new Interval(80_000, 100_000, "a6"),
                new Interval(109_000, 200_000, "a7"),
                new Interval(201_000, 210_000, "a8")
        );

        List<Interval> intervalB = Arrays.asList(
                new Interval(4_000, 10_000, "b1"),
                new Interval(10_000, 25_000, "b2"),
                new Interval(26_000, 30_000, "b3"),
                new Interval(32_000, 35_000, "b4"),
                new Interval(50_000, 60_000, "b5"),
                new Interval(90_000, 110_000, "b6")
        );
        List<Interval> result = IntervalUtils.substruct(intervalA, intervalB);
        int i = 0;
        Assert.assertEquals("failed interval check i=" + i, "a1", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 1_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 4_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a3", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 25_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 26_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a4", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 31_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 32_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a4", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 35_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 40_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a5", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 60_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 80_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a6", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 80_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 90_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a7", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 110_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 200_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a8", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 201_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 210_000, result.get(i).getStop());

        System.out.println(result);
    }


    @Test
    public void testIntersection() {
        List<Interval> intervalA = Arrays.asList(
                new Interval(1_000, 10_000, "a1"),
                new Interval(10_000, 20_000, "a2"),
                new Interval(21_000, 30_000, "a3"),
                new Interval(31_000, 40_000, "a4"),
                new Interval(60_000, 80_000, "a5"),
                new Interval(80_000, 100_000, "a6")
        );

        List<Interval> intervalB = Arrays.asList(
                new Interval(4_000, 10_000, "b1"),
                new Interval(10_000, 25_000, "b2"),
                new Interval(26_000, 30_000, "b3"),
                new Interval(32_000, 35_000, "b4"),
                new Interval(50_000, 60_000, "b5"),
                new Interval(90_000, 110_000, "b6")
        );

        List<Interval> result = IntervalUtils.intersection(intervalA, intervalB);

        int i = 0;
        Assert.assertEquals("failed interval check i=" + i, "a1", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 4_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 10_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a2", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 10_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 20_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a3", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 21_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 25_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a3", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 26_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 30_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a4", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 32_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 35_000, result.get(i).getStop());
        i++;
        Assert.assertEquals("failed interval check i=" + i, "a6", result.get(i).getData());
        Assert.assertEquals("failed interval check i=" + i, 90_000, result.get(i).getStart());
        Assert.assertEquals("failed interval check i=" + i, 100_000, result.get(i).getStop());
    }


}
