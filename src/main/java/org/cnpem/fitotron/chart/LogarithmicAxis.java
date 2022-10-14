package org.cnpem.fitotron.chart;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.util.StringConverter;

public class LogarithmicAxis extends ValueAxis<Number> {

    //private Object currentAnimationID;
    //private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
    private final StringProperty currentFormatterProperty =
            new SimpleStringProperty(this, "currentFormatter", "");
    private final LogarithmicAxis.DefaultFormatter defaultFormatter = new LogarithmicAxis.DefaultFormatter(this);

    // -------------- PUBLIC PROPERTIES --------------------------------------------------------------------------------

//    private BooleanProperty forceZeroInRange = new BooleanPropertyBase(true) {
//        @Override protected void invalidated() {
//            // This will affect layout if we are auto ranging
//            if(isAutoRanging()) {
//                requestAxisLayout();
//                invalidateRange();
//            }
//        }
//
//        @Override
//        public Object getBean() {
//            return LogarithmicAxis.this;
//        }
//
//        @Override
//        public String getName() {
//            return "forceZeroInRange";
//        }
//    };
    //public final boolean isForceZeroInRange() { return forceZeroInRange.getValue(); }
    //public final void setForceZeroInRange(boolean value) { forceZeroInRange.setValue(value); }
    //public final BooleanProperty forceZeroInRangeProperty() { return forceZeroInRange; }
    private DoubleProperty tickUnit = new StyleableDoubleProperty(5) {
        @Override protected void invalidated() {
            if(!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public CssMetaData<LogarithmicAxis,Number> getCssMetaData() {
            return LogarithmicAxis.StyleableProperties.TICK_UNIT;
        }

        @Override
        public Object getBean() {
            return LogarithmicAxis.this;
        }

        @Override
        public String getName() {
            return "tickUnit";
        }
    };
    public final double getTickUnit() { return tickUnit.get(); }
    public final void setTickUnit(double value) { tickUnit.set(value); }
    public final DoubleProperty tickUnitProperty() { return tickUnit; }

    private final DoubleProperty logUpperBound = new SimpleDoubleProperty();
    private final DoubleProperty logLowerBound = new SimpleDoubleProperty();


    // -------------- CONSTRUCTORS -------------------------------------------------------------------------------------

    public LogarithmicAxis() {
        setAutoRanging(false); //Adicionado ao construtor original
        bindLogBoundsToDefaultBounds(); //Adicionado ao construtor original
    }

    public LogarithmicAxis(double lowerBound, double upperBound, double tickUnit) {
        super(lowerBound, upperBound);
        setAutoRanging(false);//Adicionado ao construtor original
        setTickUnit(tickUnit);
        bindLogBoundsToDefaultBounds();//Adicionado ao construtor original
    }

    public LogarithmicAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        super(lowerBound, upperBound);
        setAutoRanging(false);//Adicionado ao construtor original
        setTickUnit(tickUnit);
        setLabel(axisLabel);
        bindLogBoundsToDefaultBounds();//Adicionado ao construtor original
    }

    // -------------- PROTECTED METHODS --------------------------------------------------------------------------------

    @Override protected String getTickMarkLabel(Number value) {
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) formatter = defaultFormatter;
        return formatter.toString(value);
    }


    //O auto ranging é sempre implementado pela classe herdeira do ValueAxis, logo, aqui deverá ser
    // implementada a função de auto-ranging. Consulte a classe NumberAxis para ter de inspiração.


    @Override protected Object getRange() {
        return new Object[]{
                getLowerBound(),
                getUpperBound(),
                getTickUnit(),
                getScale(),
                currentFormatterProperty.get()
        };
    }

    //Para ter animações nesta classe, utilize o código do link:
    // (http://blog.dooapp.com/2013/06/logarithmic-scale-strikes-back-in.html)

    @Override protected void setRange(Object range, boolean animate) {
        final Object[] rangeProps = (Object[]) range;
        final double lowerBound = (Double)rangeProps[0];
        final double upperBound = (Double)rangeProps[1];
        final double tickUnit = (Double)rangeProps[2];
        final double scale = (Double)rangeProps[3];
        final String formatter = (String)rangeProps[4];
        currentFormatterProperty.set(formatter);
        final double oldLowerBound = getLowerBound();
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
        setTickUnit(tickUnit);
//        if(animate) {
//            animator.stop(currentAnimationID);
//            currentAnimationID = animator.animate(
//                    new KeyFrame(Duration.ZERO,
//                            new KeyValue(currentLowerBound, oldLowerBound),
//                            new KeyValue(scalePropertyImpl(), getScale())
//                    ),
//                    new KeyFrame(Duration.millis(700),
//                            new KeyValue(currentLowerBound, lowerBound),
//                            new KeyValue(scalePropertyImpl(), scale)
//                    )
//            );
//        } else {
            currentLowerBound.set(lowerBound);
            setScale(scale);
//        }
    }

    //Adicionado à classe original
    private void bindLogBoundsToDefaultBounds() {
        logLowerBound.bind(new DoubleBinding() {

            {
                super.bind(lowerBoundProperty());
            }

            @Override
            protected double computeValue() {
                return Math.log10(lowerBoundProperty().get());
            }
        });
        logUpperBound.bind(new DoubleBinding() {

            {
                super.bind(upperBoundProperty());
            }

            @Override
            protected double computeValue() {
                return Math.log10(upperBoundProperty().get());
            }
        });
    }

    @Override //Adicionado à classe original
    protected List<Number> calculateMinorTickMarks() {
        Object range = getRange();
        List<Number> minorTickMarksPositions = new ArrayList<Number>();
        if (range != null) {

            Number upperBound = (Double) ((Object[]) range)[1];
            double logUpperBound = Math.log10(upperBound.doubleValue());
            int minorTickMarkCount = getMinorTickCount();

            for (double i = 0; i <= logUpperBound; i += 1) {
                for (double j = 0; j <= 9; j += (1. / minorTickMarkCount)) {
                    double value = j * Math.pow(10, i);
                    minorTickMarksPositions.add(value);
                }
            }
        }
        return minorTickMarksPositions;
    }





    //Este método precisa ser alterado de modo que considere a quantidade de
    //itens a serem exibidos (tickUnit) e ao mesmo tempo dê uma distância segura entre os elementos
    //Use a implementação deste método da classe NumberAxis de Exemplo.
    @Override //Adicionado à classe original
    protected List<Number> calculateTickValues(double length, Object range) {
        List<Number> tickPositions = new ArrayList<Number>();
        if (range != null) {
            Number lowerBound = (Double) ((Object[]) range)[0];
            Number upperBound = (Double) ((Object[]) range)[1];
            double logLowerBound = Math.log10(lowerBound.doubleValue());
            double logUpperBound = Math.log10(upperBound.doubleValue());

            for (double i = 0; i <= logUpperBound; i += 1) {
                for (double j = 1; j <= 9; j++) {
                    double value = j * Math.pow(10, i);
//                    if(value>50000 && value<100000){
//                        break;
//                    } else if (value>10000) {
//                        if(j!=1){
//                            break;
//                        }
//                    }
                    tickPositions.add(value);
                }
            }
        }
        return tickPositions;
    }






    @Override//Adicionado à classe original
    public Number getValueForDisplay(double displayPosition) {
        double delta = logUpperBound.get() - logLowerBound.get();
        if (getSide().isVertical()) {
            return Math.pow(10, (((displayPosition - getHeight()) / -getHeight()) * delta) + logLowerBound.get());
        } else {
            return Math.pow(10, (((displayPosition / getWidth()) * delta) + logLowerBound.get()));
        }
    }

    @Override//Adicionado à classe original
    public double getDisplayPosition(Number value) {
        double delta = logUpperBound.get() - logLowerBound.get();
        double deltaV = Math.log10(value.doubleValue()) - logLowerBound.get();
        if (getSide().isVertical()) {
            return (1. - ((deltaV) / delta)) * getHeight();
        } else {
            return ((deltaV) / delta) * (getWidth());
        }
    }

    @Override protected Dimension2D measureTickMarkSize(Number value, Object range) {
        final Object[] rangeProps = (Object[]) range;
        final String formatter = (String)rangeProps[4];
        return measureTickMarkSize(value, getTickLabelRotation(), formatter);
    }

    private Dimension2D measureTickMarkSize(Number value, double rotation, String numFormatter) {
        String labelText;
        StringConverter<Number> formatter = getTickLabelFormatter();
        if (formatter == null) formatter = defaultFormatter;
        if(formatter instanceof NumberAxis.DefaultFormatter) {
            labelText = ((LogarithmicAxis.DefaultFormatter)formatter).toString(value, numFormatter);
        } else {
            labelText = formatter.toString(value);
        }

        Dimension2D dimension2D = measureTickMarkLabelSize(labelText, rotation);

        return dimension2D;
    }

    // -------------- STYLESHEET HANDLING ------------------------------------------------------------------------------

    private static class StyleableProperties {
        private static final CssMetaData<LogarithmicAxis,Number> TICK_UNIT =
                new CssMetaData<LogarithmicAxis,Number>("-fx-tick-unit",
                        SizeConverter.getInstance(), 5.0) {

                    @Override
                    public boolean isSettable(LogarithmicAxis n) {
                        return n.tickUnit == null || !n.tickUnit.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(LogarithmicAxis n) {
                        return (StyleableProperty<Number>)(WritableValue<Number>)n.tickUnitProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(ValueAxis.getClassCssMetaData());
            styleables.add(TICK_UNIT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return LogarithmicAxis.StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    // -------------- INNER CLASSES ------------------------------------------------------------------------------------

    /**
     * Default number formatter for NumberAxis, this stays in sync with auto-ranging and formats values appropriately.
     * You can wrap this formatter to add prefixes or suffixes;
     * @since JavaFX 2.0
     */
    public static class DefaultFormatter extends StringConverter<Number> {
        private DecimalFormat formatter;
        private String prefix = null;
        private String suffix = null;

        public DefaultFormatter(final LogarithmicAxis axis) {
            formatter = axis.isAutoRanging()? new DecimalFormat(axis.currentFormatterProperty.get()) : new DecimalFormat();
            final ChangeListener<Object> axisListener = (observable, oldValue, newValue) -> {
                formatter = axis.isAutoRanging()? new DecimalFormat(axis.currentFormatterProperty.get()) : new DecimalFormat();
            };
            axis.currentFormatterProperty.addListener(axisListener);
            axis.autoRangingProperty().addListener(axisListener);
        }

        public DefaultFormatter(LogarithmicAxis axis, String prefix, String suffix) {
            this(axis);
            this.prefix = prefix;
            this.suffix = suffix;
        }

        @Override public String toString(Number object) {
            return toString(object, formatter);
        }

        private String toString(Number object, String numFormatter) {
            if (numFormatter == null || numFormatter.isEmpty()) {
                return toString(object, formatter);
            } else {
                return toString(object, new DecimalFormat(numFormatter));
            }
        }

        private String toString(Number object, DecimalFormat formatter) {
            if (prefix != null && suffix != null) {
                return prefix + formatter.format(object) + suffix;
            } else if (prefix != null) {
                return prefix + formatter.format(object);
            } else if (suffix != null) {
                return formatter.format(object) + suffix;
            } else {
                return formatter.format(object);
            }
        }

        @Override public Number fromString(String string) {
            try {
                int prefixLength = (prefix == null)? 0: prefix.length();
                int suffixLength = (suffix == null)? 0: suffix.length();
                return formatter.parse(string.substring(prefixLength, string.length() - suffixLength));
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public class IllegalLogarithmicRangeException extends Exception {

        public IllegalLogarithmicRangeException(String message) {
            super(message);
        }
    }
}


