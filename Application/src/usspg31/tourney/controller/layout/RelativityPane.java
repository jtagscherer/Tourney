package usspg31.tourney.controller.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import usspg31.tourney.controller.layout.RelativityPane.RelativityConstraints.LayoutDistance;

/**
 * RelativityPane allows the children to be positioned and resized relative to
 * the size of the parent. If the RelativityPane has a border and/or padding
 * set, the children will be arranged inside these insets.
 * <p>
 * The RelativityPane lays out each managed child regardless of the child's
 * visible property value - unmanaged children are ignored for all layout
 * calculations.
 * </p>
 * <p>
 * RelativePanes may be styled with backgrounds and borders using CSS. See
 * {@link javafx.scene.layout.Region Region} superclass for details.
 * </p>
 *
 * <h4>Relative Constraints</h4>
 * <p>
 * The application sets relativity constraints on each child to configure the
 * position within the RelativityPane.
 * </p>
 *
 * <p>
 * A relativity constraint can consist of a percentage and up to two pixel
 * values. The percentage defines the preferred distance from the appropriate
 * border of the relativity pane. With the first pixel value, the minimum
 * distance of the child from the border can be set, the second pixel value
 * represents the maximum distance from the border.
 * </p>
 *
 * <p>
 * Relativity constraints for the top border can easily be set as a String via
 * 
 * <pre>
 * setRelTop
 * </pre>
 * 
 * (for right, bottom and left borders use
 * 
 * <pre>setRelRight
 * 
 * <pre>,
 * 
 * <pre>
 * setRelBottom
 * </pre>
 * 
 * and
 * 
 * <pre>
 * setRelLeft
 * </pre>
 * 
 * respectively)
 * </p>
 *
 * <p>
 * The relativity strings have to be built like
 * 
 * <pre>
 * ([number]%)?([number]px([number]px)?)?
 * </pre>
 * 
 * .<br>
 * If the relativity string is an empty string or only consists of whitespace,
 * the constraint will be unset.
 * </p>
 *
 * <p>
 * For example, the following string will tell the relativity pane to position
 * the child 25% from the border, but keep it at least 10px away from it and at
 * all times within 100px from the border:
 * </p>
 * 
 * <pre>
 * 25% 10px 100px
 * </pre>
 *
 * <p>
 * RelativityPane Example:
 * 
 * <pre>
 * <code>
 * RelativityPane relativityPane = new RelativityPane();
 * // List should cover the top left quarter of the relativePane
 * ListView list = new ListView();
 * <b>RelativityPane.setRelTop(list, "0px");
 * RelativityPane.setRelLeft(list, "0px");
 * RelativityPane.setRelRight(list, "50%");
 * RelativityPane.setRelBottom(list, "50%");</b>
 * relativityPane.getChildren().add(list);
 * </code>
 * </pre>
 * 
 * </p>
 *
 * @author Jonas Auer
 * @version 1.0
 */
public class RelativityPane extends Pane {

    private static final Logger log = Logger.getLogger(RelativityPane.class
            .getName());

    public static class RelativityConstraints {
        public static class LayoutDistance {
            public static class MalformedLayoutDescription extends
                    RuntimeException {
                private static final long serialVersionUID = 1172054622086593944L;

                public MalformedLayoutDescription(String input) {
                    super(
                            "The given input could not be parsed to a LayoutDistance.\n"
                                    + "Was given: \""
                                    + input
                                    + "\"\n"
                                    + "Expected: \"<percentage>?(<distance>(<distance>)?)?\"\n");
                }
            }

            public static final LayoutDistance NOTSET = new LayoutDistance();

            private final Double percent;
            private final Double min;
            private final Double max;

            // matches a double or integer, scientific notation not supported
            private static final String doubleRegex = "(?:[+\\-]?\\d*\\.\\d+)|(?:[+\\-]?\\d+)";
            private static final Pattern distancePattern = Pattern.compile("^"
                    // matches a single percentage
                    + "(?:(?<perc>" + doubleRegex + ")%)?\\W*"
                    // matches one to two pixel distances
                    + "(?:(?<pxMin>" + doubleRegex + ")px(?:\\W*(?<pxMax>"
                    + doubleRegex + ")px)?)?" + "$");

            private static LayoutDistance fromString(String string) {
                string = string.trim();
                Matcher m = distancePattern.matcher(string);
                if (!m.matches()) {
                    RuntimeException e = new MalformedLayoutDescription(string);
                    log.log(Level.WARNING, e.getMessage(), e);
                    throw e;
                }
                String perc = m.group("perc");
                String pxMin = m.group("pxMin");
                String pxMax = m.group("pxMax");
                if (perc == null && pxMin == null && pxMax == null) {
                    return LayoutDistance.NOTSET;
                }
                return new LayoutDistance((perc != null
                        ? Double.parseDouble(perc)
                        : null), (pxMin != null
                        ? Double.parseDouble(pxMin)
                        : null), (pxMax != null
                        ? Double.parseDouble(pxMax)
                        : null));
            }

            private LayoutDistance() {
                this.percent = null;
                this.min = null;
                this.max = null;
            }

            public LayoutDistance(Double percent, Double min, Double max) {
                if (percent == null && min == null && max == null) {
                    RuntimeException e = new RuntimeException("At least a "
                            + "percentage or a minimum value must to be set");
                    log.log(Level.WARNING, e.getMessage(), e);
                    throw e;
                }
                if (percent == null && min == null && max != null) {
                    min = max;
                    max = null;
                }
                if (min != null && max != null && min > max) {
                    Double tmp = min;
                    min = max;
                    max = tmp;
                }
                this.percent = percent;
                this.min = min;
                this.max = max;
            }

            public Double getPercent() {
                return this.percent;
            }

            public Double getMin() {
                return this.min;
            }

            public Double getMax() {
                return this.max;
            }

            public boolean isSet() {
                return this.percent != null || this.min != null;
            }

            @Override
            public String toString() {
                return (this.percent != null ? this.percent + "%" : "")
                        + (this.percent != null && this.min != null ? " " : "")
                        + (this.min != null ? this.min + "px" : "")
                        + (this.min != null && this.max != null ? " "
                                + this.max + "px" : "");
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof LayoutDistance)) {
                    return false;
                }
                LayoutDistance dist = (LayoutDistance) obj;
                return dist.percent.equals(this.percent)
                        && dist.min.equals(this.min)
                        && dist.max.equals(this.max);
            }
        }

        private LayoutDistance top = LayoutDistance.NOTSET;
        private LayoutDistance right = LayoutDistance.NOTSET;
        private LayoutDistance bottom = LayoutDistance.NOTSET;
        private LayoutDistance left = LayoutDistance.NOTSET;

        public RelativityConstraints setTop(LayoutDistance top) {
            this.top = top;
            return this;
        }

        public RelativityConstraints setRight(LayoutDistance right) {
            this.right = right;
            return this;
        }

        public RelativityConstraints setBottom(LayoutDistance bottom) {
            this.bottom = bottom;
            return this;
        }

        public RelativityConstraints setLeft(LayoutDistance left) {
            this.left = left;
            return this;
        }

        public LayoutDistance getTop() {
            return this.top;
        }

        public LayoutDistance getRight() {
            return this.right;
        }

        public LayoutDistance getBottom() {
            return this.bottom;
        }

        public LayoutDistance getLeft() {
            return this.left;
        }

        @Override
        public String toString() {
            return "RelativityConstraints:" + " Top: " + this.top + " Right: "
                    + this.right + " Bottom: " + this.bottom + " Left: "
                    + this.left;
        }
    }

    /**
     * Sets the child's relativity value for the top border of the
     * RelativityPane
     * 
     * @param child
     *            the child node of a relativity pane
     * @param value
     *            the relativity of the node
     */
    public static void setRelTop(Node child, String relativity) {
        RelativityConstraints constraints = getConstraints(child);
        constraints.setTop(LayoutDistance.fromString(relativity));
        log.finer("Setting top layout distance for " + child + " to "
                + constraints.getTop());
    }

    /**
     * Returns the child's relativity value for the top border of the
     * RelativityPane if set
     * 
     * @param child
     *            the child node of a relativity pane
     * @return the relativity string of the node for the top border
     */
    public static String getRelTop(Node child) {
        RelativityConstraints c = constraints.get(child);
        return c != null ? c.getTop().toString() : null;
    }

    /**
     * Sets the child's relativity value for the right border of the
     * RelativityPane
     * 
     * @param child
     *            the child node of a relativity pane
     * @param value
     *            the relativity of the node
     */
    public static void setRelRight(Node child, String relativity) {
        RelativityConstraints constraints = getConstraints(child);
        constraints.setRight(LayoutDistance.fromString(relativity));
        log.finer("Setting right layout distance for " + child + " to "
                + constraints.getRight());
    }

    /**
     * Returns the child's relativity value for the right border of the
     * RelativityPane if set
     * 
     * @param child
     *            the child node of a relativity pane
     * @return the relativity string of the node for the right border
     */
    public static String getRelRight(Node child) {
        RelativityConstraints c = constraints.get(child);
        return c != null ? c.getRight().toString() : null;
    }

    /**
     * Sets the child's relativity value for the bottom border of the
     * RelativityPane
     * 
     * @param child
     *            the child node of a relativity pane
     * @param value
     *            the relativity of the node
     */
    public static void setRelBottom(Node child, String relativity) {
        RelativityConstraints constraints = getConstraints(child);
        constraints.setBottom(LayoutDistance.fromString(relativity));
        log.finer("Setting bottom layout distance for " + child + " to "
                + constraints.getBottom());
    }

    /**
     * Returns the child's relativity value for the bottom border of the
     * RelativityPane if set
     * 
     * @param child
     *            the child node of a relativity pane
     * @return the relativity string of the node for the bottom border
     */
    public static String getRelBottom(Node child) {
        RelativityConstraints c = constraints.get(child);
        return c != null ? c.getBottom().toString() : null;
    }

    /**
     * Sets the child's relativity value for the left border of the
     * RelativityPane
     * 
     * @param child
     *            the child node of a relativity pane
     * @param value
     *            the relativity of the node
     */
    public static void setRelLeft(Node child, String relativity) {
        RelativityConstraints constraints = getConstraints(child);
        constraints.setLeft(LayoutDistance.fromString(relativity));
        log.finer("Setting left layout distance for " + child + " to "
                + constraints.getLeft());
    }

    /**
     * Returns the child's relativity value for the left border of the
     * RelativityPane if set
     * 
     * @param child
     *            the child node of a relativity pane
     * @return the relativity string of the node for the left border
     */
    public static String getRelLeft(Node child) {
        RelativityConstraints c = constraints.get(child);
        return c != null ? c.getLeft().toString() : null;
    }

    private static final Map<Node, RelativityConstraints> constraints = new HashMap<>();

    private static RelativityConstraints getConstraints(Node node) {
        RelativityConstraints constraints = RelativityPane.constraints
                .get(node);
        if (constraints == null) {
            constraints = new RelativityConstraints();
            RelativityPane.constraints.put(node, constraints);
        }
        return constraints;
    }

    /**
     * Clears all relativity pane constraints off the child node.
     * 
     * @param child
     *            the child node
     */
    public static void clearConstraints(Node node) {
        constraints.remove(node);
    }

    public RelativityPane() {
        super();
    }

    public RelativityPane(Node... children) {
        super();
        this.getChildren().addAll(children);
    }

    @Override
    protected void layoutChildren() {
        Insets insets = this.getInsets();
        final List<Node> children = this.getManagedChildren();

        final double effectiveWidth = this.getWidth() - insets.getLeft()
                - insets.getRight();
        final double effectiveHeight = this.getHeight() - insets.getTop()
                - insets.getBottom();

        double prefHeight = 0;
        double prefWidth = 0;

        for (Node child : children) {
            RelativityConstraints constraints = getConstraints(child);

            LayoutDistance top = constraints.getTop();
            LayoutDistance right = constraints.getRight();
            LayoutDistance bottom = constraints.getBottom();
            LayoutDistance left = constraints.getLeft();

            Double t = null;
            Double r = null;
            Double b = null;
            Double l = null;

            if (top.isSet()) {
                t = this.calculateDistance(effectiveHeight, top);
            }
            if (right.isSet()) {
                r = this.calculateDistance(effectiveWidth, right);
            }
            if (bottom.isSet()) {
                b = this.calculateDistance(effectiveHeight, bottom);
            }
            if (left.isSet()) {
                l = this.calculateDistance(effectiveWidth, left);
            }

            double w;
            double h;
            if (child.getContentBias() == Orientation.VERTICAL) {
                h = this.calculateHeight(effectiveHeight, t, b, child);
                w = this.calculateWidth(effectiveWidth, l, r, child, h);
            } else if (child.getContentBias() == Orientation.HORIZONTAL) {
                w = this.calculateWidth(effectiveWidth, l, r, child);
                h = this.calculateHeight(effectiveHeight, t, b, child, w);
            } else {
                h = this.calculateHeight(effectiveHeight, t, b, child);
                w = this.calculateWidth(effectiveWidth, l, r, child);
            }

            if (t != null && b != null && (t + h + b != effectiveHeight)) {
                double delta = (effectiveHeight - t - h - b) / 2;
                t += delta;
                b += delta;
            } else if (t == null && b != null) {
                t = effectiveHeight - b - h;
            } else if (t == null && b == null) {
                t = 0.0;
            }

            if (l != null && r != null && (l + w + r != effectiveWidth)) {
                double delta = (effectiveWidth - l - w - r) / 2;
                l += delta;
                r += delta;
            } else if (l == null && r != null) {
                l = effectiveWidth - r - w;
            } else if (l == null && r == null) {
                l = 0.0;
            }

            if (this.isSnapToPixel()) {
                // snap the used distances to match whole pixels
                l = (double) Math.round(l);
                w = Math.ceil(w);
                t = (double) Math.round(t);
                h = Math.ceil(h);
            }

            log.finest("Laying out " + child + " to x=" + l + ", y=" + t
                    + ", width=" + w + ", height=" + h);
            child.resizeRelocate(l, t, w, h);
        }

        prefWidth += insets.getLeft() + insets.getRight();
        prefHeight += insets.getTop() + insets.getBottom();

        log.finer("Updating preferred size to " + prefWidth + "x" + prefHeight);
        this.setPrefWidth(prefWidth);
        this.setPrefHeight(prefHeight);
    }

    private double calculateWidth(double totalWidth, Double left, Double right,
            Node child) {
        return this.calculateWidth(totalWidth, left, right, child, -1);
    }

    private double calculateWidth(double totalWidth, Double left, Double right,
            Node child, double height) {
        double min = child.minWidth(height);
        double max = child.maxWidth(height);
        double pref;
        if (left == null || right == null) {
            pref = child.prefWidth(height);
        } else {
            pref = totalWidth - left - right;
        }
        return Math.max(min, Math.min(pref, max));
    }

    private double calculateHeight(double totalHeight, Double top,
            Double bottom, Node child) {
        return this.calculateHeight(totalHeight, top, bottom, child, -1);
    }

    private double calculateHeight(double totalHeight, Double top,
            Double bottom, Node child, double width) {
        double min = child.minHeight(width);
        double max = child.maxHeight(width);
        double pref;
        if (top == null || bottom == null) {
            pref = child.prefHeight(width);
        } else {
            pref = totalHeight - top - bottom;
        }
        return Math.max(min, Math.min(pref, max));
    }

    private double calculateDistance(double maxSize, LayoutDistance distance) {
        if (distance.getPercent() == null) {
            return distance.getMin();
        }
        double dist = maxSize * distance.getPercent() * .01;
        if (distance.getMin() != null && distance.getMin() > dist) {
            dist = distance.getMin();
        } else if (distance.getMax() != null && distance.getMax() < dist) {
            dist = distance.getMax();
        }
        return dist;
    }
}
