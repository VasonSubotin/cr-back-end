package com.sm.client.utils.intervals;

import java.util.ArrayList;
import java.util.List;

public class IntervalUtils {

    public static List<Interval> substruct(List<Interval> intervalsA, List<Interval> intervalsB) {
        List<Interval> result = new ArrayList<>();
        int i = 0, j = 0;

        Interval a = intervalsA.get(i);
        Interval b = intervalsB.get(j);

        while (i < intervalsA.size() && j < intervalsB.size()) {
            if (j < intervalsB.size()) {
                b = intervalsB.get(j);
            }

            if (i < intervalsA.size()) {
                a = intervalsA.get(i);
            }

            //---------(B-------B)------(A------A)-------
            if (a.getStart() >= b.getStop()) {
                j++;
                continue;
            }

            //---------(A-------A)----(B------B)-------
            if (b.getStart() >= a.getStop()) {
                result.add(a);
                i++;
                continue;
            }
            //---------(A-----------(B------....----A)-------
            if (a.getStart() < b.getStart() && b.getStart() <= a.getStop()) {
                Interval cloneA = new Interval();
                cloneA.setData(a.getData());
                cloneA.setStart(a.getStart());
                cloneA.setStop(b.getStart());
                result.add(cloneA);
                //---------(A-------(B------B)-----A)-------
                if (a.getStop() > b.getStop()) {
                    // need to split
                    a.setStart(b.getStop());
                    j++;
                }else{
                    //---------(A-------(B--------B)-------A)-----
                    i++;
                }

                continue;
            }
            //---------(B-----------(A------....----B)-------
            if (b.getStart() <= a.getStart() && a.getStart() < b.getStop()) {
                //---------(B-------(A--------B)-------A)-----
                if (a.getStop() > b.getStop()) {
                    // need to split
                    a.setStart(b.getStop());
                    j++;
                } else {
                    //---------(B-------(A--------A)-------B)-----
                    i++;
                }
                continue;
            }
        }

        for (; i < intervalsA.size(); i++) {
            result.add(intervalsA.get(i));
        }
        return result;
    }


    public static List<Interval> intersection(List<Interval> intervalsA, List<Interval> intervalsB) {
        List<Interval> result = new ArrayList<>();

        int i = 0, j = 0;
        Interval a = null;
        Interval b = null;

        while (i < intervalsA.size() && j < intervalsB.size()) {
            if (i < intervalsA.size()) {
                a = intervalsA.get(i);
            }
            if (j < intervalsB.size()) {
                b = intervalsB.get(j);
            }

            //-------(B-------B)----(A-------A)-----
            if (a.getStart() >= b.getStop()) {
                j++;
                continue;
            }
            //-------(A-------A)----(B-------B)-----
            if (b.getStart() >= a.getStop()) {
                i++;
                continue;
            }
            //-------(A------(B------.....-----A)---------
            if (a.getStart() <= b.getStart() && b.getStart() < a.getStop()) {
                //-------(A------(B------B)-----A)---------
                if (b.getStop() < a.getStop()) {
                    Interval cloneA = new Interval();
                    cloneA.setStart(b.getStart());
                    cloneA.setStop(b.getStop());
                    cloneA.setData(a.getData());
                    result.add(cloneA);
                    j++;
                } else {
                    //-------(A------(B-----------A)---------B)-----
                    Interval cloneA = new Interval();
                    cloneA.setStart(b.getStart());
                    cloneA.setStop(a.getStop());
                    cloneA.setData(a.getData());
                    result.add(cloneA);
                    i++;
                }
                continue;
            }

            //-------(B------(A------.....-----B)---------
            if (b.getStart() <= a.getStart() && a.getStart() < b.getStop()) {
                //-------(B------(A------A)-----B)---------
                if (a.getStop() < b.getStop()) {
                    result.add(a);
                    i++;
                } else {
                    //-------(B------(A-----------B)------A)---
                    Interval cloneA = new Interval();
                    cloneA.setStart(a.getStart());
                    cloneA.setStop(b.getStop());
                    cloneA.setData(a.getData());
                    result.add(cloneA);
                    j++;
                }
            }
        }
        return result;
    }


}
