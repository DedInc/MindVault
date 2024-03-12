package com.github.dedinc.mindvault.ui.frames;

import com.github.dedinc.mindvault.core.Intervals;
import com.github.dedinc.mindvault.core.Session;
import com.github.dedinc.mindvault.core.Time;
import com.github.dedinc.mindvault.core.objects.Card;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgettingGraphFrame extends JFrame {
    private Session session;

    public ForgettingGraphFrame(Session session) {
        this.session = session;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Ebbinghaus Forgetting Graph");
        setPreferredSize(new Dimension(800, 600));
        pack();
    }

    @Override
    public void setVisible(boolean visible) {
        TimeSeriesCollection dataset = createDataset();
        if (dataset == null && visible) {
            JOptionPane.showMessageDialog(null, "No data to display!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.setVisible(visible);
        if (visible) {
            JFreeChart chart = createChart(dataset);
            ChartPanel chartPanel = new ChartPanel(chart);
            add(chartPanel);
        }
    }

    private TimeSeriesCollection createDataset() {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries forgettingSeries = new TimeSeries("Forgetting Curve");
        TimeSeries revisionSeries = new TimeSeries("Revisions");
        TimeSeries violationSeries = new TimeSeries("Interval Violations");
        TimeSeries futureRevisionSeries = new TimeSeries("Next Revision");
        TimeSeries currentTimeSeries = new TimeSeries("Current Time");

        Card commonCard = getCommonCard();
        if (commonCard == null) return null;

        long learnDate = commonCard.getLearnDate();
        long currentDate = Time.getUnix();
        long[] reviseDates = commonCard.getReviseDates();
        int[] intervals = Intervals.intervals;
        double[] retentionRates = {100, 90, 80, 70, 60, 50, 40, 30};

        forgettingSeries.addOrUpdate(new Day(new Date(learnDate * 1000L)), 100);
        for (int i = 0; i < intervals.length; i++) {
            double daysSinceLearning = intervals[i];
            double retentionRate = retentionRates[i];
            forgettingSeries.addOrUpdate(new Day(new Date((learnDate + (long) (daysSinceLearning * 86400)) * 1000L)), retentionRate);
        }

        for (long reviseDate : reviseDates) {
            if (reviseDate > 0) {
                double revisionDaysSinceLearning = (reviseDate - learnDate) / 86400.0;
                double revisionRetentionRate = calculateRetentionRate(revisionDaysSinceLearning, intervals, retentionRates);
                forgettingSeries.addOrUpdate(new Day(new Date(reviseDate * 1000L)), revisionRetentionRate);
                forgettingSeries.addOrUpdate(new Day(new Date(reviseDate * 1000L)), 100);
                revisionSeries.addOrUpdate(new Day(new Date(reviseDate * 1000L)), 100);
            }
        }

        long lastRevisionDate = reviseDates.length > 0 ? reviseDates[reviseDates.length - 1] : learnDate;
        for (int i = 0; i < intervals.length; i++) {
            long futureRevisionDate = (long) (lastRevisionDate + intervals[i] * 86400);
            if (futureRevisionDate > currentDate) {
                futureRevisionSeries.addOrUpdate(new Day(new Date(futureRevisionDate * 1000L)), 100);
                violationSeries.addOrUpdate(new Day(new Date((futureRevisionDate + (long) (intervals[i] * 86400 / 3)) * 1000L)), 100);
                break;
            }
            lastRevisionDate = futureRevisionDate;
        }
        currentTimeSeries.addOrUpdate(new Day(new Date(currentDate * 1000L)), 100);

        dataset.addSeries(forgettingSeries);
        dataset.addSeries(revisionSeries);
        dataset.addSeries(violationSeries);
        dataset.addSeries(futureRevisionSeries);
        dataset.addSeries(currentTimeSeries);

        return dataset;
    }

    private double calculateRetentionRate(double daysSinceLearning, int[] intervals, double[] retentionRates) {
        for (int i = 0; i < intervals.length; i++) {
            if (daysSinceLearning <= intervals[i]) {
                return retentionRates[i];
            }
        }
        return retentionRates[retentionRates.length - 1];
    }

    public Card getCommonCard() {
        List<Card> cards = session.getAllCards();
        List<Long> learns = new ArrayList<>();
        Map<Card, long[]> revises = new HashMap<>();
        for (Card card : cards) {
            if (card.getLearnDate() > 0) {
                learns.add(card.getLearnDate());
                revises.put(card, card.getReviseDates());
            }
        }
        if (learns.isEmpty()) {
            return null;
        }
        long averageLearnDate = learns.stream().mapToLong(Long::longValue).sum() / learns.size();
        Card commonCard = null;
        long minDifference = Long.MAX_VALUE;
        for (Card card : revises.keySet()) {
            long difference = Math.abs(card.getLearnDate() - averageLearnDate);
            if (difference < minDifference) {
                minDifference = difference;
                commonCard = card;
            }
        }
        return commonCard;
    }

    private JFreeChart createChart(TimeSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Ebbinghaus Forgetting Graph", "Date", "Retention (%)",
                dataset, true, true, false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        Color forgettingColor = new Color(150, 0, 255);
        Color revisionColor = new Color(0, 255, 0);
        Color futureRevisionColor = new Color(0, 0, 255);
        Color violationColor = new Color(255, 0, 0);
        Color currentTimeColor = new Color(255, 165, 0);

        renderer.setSeriesPaint(0, forgettingColor);
        renderer.setSeriesPaint(1, revisionColor);
        renderer.setSeriesPaint(2, violationColor);
        renderer.setSeriesPaint(3, futureRevisionColor);
        renderer.setSeriesPaint(4, currentTimeColor);

        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesShapesVisible(3, true);
        renderer.setSeriesShapesVisible(4, true);

        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(1.5f));
        renderer.setSeriesStroke(2, new BasicStroke(1.5f));
        renderer.setSeriesStroke(3, new BasicStroke(1.5f));
        renderer.setSeriesStroke(4, new BasicStroke(1.5f));
        plot.setRenderer(renderer);
        DateAxis domainAxis = new DateAxis("Date");
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM"));
        domainAxis.setVerticalTickLabels(false);
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));
        domainAxis.setTickLabelsVisible(true);
        domainAxis.setTickMarksVisible(true);
        plot.setDomainAxis(domainAxis);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 110);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        return chart;
    }
}