/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Abhijit Parida <abhijitparida.me@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package app.abhijit.iter.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Formatter;

/**
 * This class represents a subject.
 */
public class Subject {

    private final String name;
    private final String code;
    private final long lastUpdated;
    private final int labPresent;
    private final int labTotal;
    private final int theoryPresent;
    private final int theoryTotal;

    public Subject(@NonNull String name, @NonNull String code, long lastUpdated, int labPresent,
                   int labTotal, int theoryPresent, int theoryTotal) {
        this.name = name;
        this.code = code;
        this.lastUpdated = lastUpdated;
        this.labPresent = labPresent;
        this.labTotal = labTotal;
        this.theoryPresent = theoryPresent;
        this.theoryTotal = theoryTotal;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    @NonNull
    public String getCode() {
        return this.code;
    }

    public long getLastUpdated() {
        return this.lastUpdated;
    }

    public int getPresent() {
        return this.labPresent + this.theoryPresent;
    }

    public int getAbsent() {
        return this.getTotal() - this.getPresent();
    }

    public int getTotal() {
        return this.theoryTotal + this.labTotal;
    }

    public int getLabPresent() {
        return this.labPresent;
    }

    public int getLabTotal() {
        return this.labTotal;
    }

    public int getTheoryPresent() {
        return this.theoryPresent;
    }

    public int getTheoryTotal() {
        return this.theoryTotal;
    }

    public double getAttendance() {
        double present = (double) this.getPresent();
        double total = (double) this.getTotal();

        if (Double.compare(total, 0.0) > 0) {
            return (present / total) * 100;
        } else {
            return 0.0;
        }
    }

    @NonNull
    public String getBunkStats(int minimumAttendance, boolean extendedStats) {
        StringBuilder bunkStats = new StringBuilder("");
        ArrayList<String> bunk = new ArrayList<>();
        ArrayList<String> need = new ArrayList<>();
        int attendance = (int) this.getAttendance();
        int classes = this.getTotal();
        int classesPresent = this.getPresent();
        int classesAbsent = this.getAbsent();
        int lastDays;
        int approxTotalClasses = 55;

        if (classes != 0 && attendance <= minimumAttendance) {
            bunk.add("DO NOT BUNK ANY MORE CLASSES\n");
        } else {
            lastDays = -1;
            for (int a = minimumAttendance; a < attendance; a += 5) {
                int daysBunk = (int) ((100 * classesPresent / (double) a) - (double) classes);
                if (daysBunk == lastDays) continue; else lastDays = daysBunk;
                if (daysBunk > 0) {
                    bunk.add(new Formatter().format("Bunk %d%s %s for %d%% attendance\n",
                            daysBunk,  classesAbsent == 0 ? "" : " more",
                            daysBunk == 1 ? "class" : "classes", a).toString());
                }
            }
        }

        if (classes != 0) {
            int nextAttendance = (attendance + 4) / 5 * 5;
            if (nextAttendance == attendance) nextAttendance = attendance + 5;
            if (nextAttendance < minimumAttendance) nextAttendance = minimumAttendance;
            lastDays = -1;
            for (int a = nextAttendance; a <= 95; a += 5) {
                int daysNeed = (int) ((a * classes - 100 * classesPresent) / (double) (100 - a));
                if (daysNeed == lastDays) continue;
                else lastDays = daysNeed;
                if (daysNeed > 0 && (daysNeed + classes <= approxTotalClasses)) {
                    need.add(new Formatter().format("Need %d more %s for %d%% attendance\n",
                            daysNeed, daysNeed == 1 ? "class" : "classes", a).toString());
                }
            }
        }

        if (extendedStats) {
            for (int i = 0; i < bunk.size(); i++) {
                bunkStats.append(bunk.get(i));
            }
            for (int i = 0; i < need.size(); i++) {
                bunkStats.append(need.get(i));
            }
        } else {
            if (!bunk.isEmpty()) bunkStats.append(bunk.get(bunk.size() - 1));
            if (!need.isEmpty()) bunkStats.append(need.get(0));
        }
        if (bunkStats.length() != 0) bunkStats.setLength(bunkStats.length() - 1);

        return bunkStats.toString();
    }
}
