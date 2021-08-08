/*
 * (C) 2021 Michaël Michaud
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * m.michael.michaud@orange.fr
 */

package fr.michaelm.jump.plugin.match;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.*;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.workbench.Logger;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.StandardCategoryNames;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.plugin.ThreadedBasePlugIn;
import com.vividsolutions.jump.workbench.ui.*;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.RingVertexStyle;
import fr.michaelm.jump.plugin.match.matcher.*;
import fr.michaelm.util.text.RuleFormatException;
import fr.michaelm.util.text.RuleNotFoundException;
import fr.michaelm.util.text.RuleRegistry;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.openjump.core.ui.plugin.tools.aggregate.Aggregator;
import org.openjump.core.ui.plugin.tools.aggregate.Aggregators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.*;


/**
 * PlugIn to find features of a source layer matching features of a target layer
 * (or reference layer) and optionally transfer attributes between matching
 * features.
 * @author Michaël Michaud
 */
public class MatchingPlugIn extends ThreadedBasePlugIn {

    private static final I18N i18n = I18N.getInstance("fr.michaelm.jump.plugin.match");

    private final String P_SRC_LAYER                = "SourceLayer";
    private final String P_SINGLE_SRC               = "SingleSource";
    private final String P_TGT_LAYER                = "TargetLayer";
    private final String P_SINGLE_TGT               = "SingleTarget";
    private final String P_GEOMETRY_MATCHER         = "GeometryMatcher";
    private final String P_MAX_GEOM_DISTANCE        = "MaximumGeometriesDistance";
    private final String P_MIN_GEOM_OVERLAP         = "MinimumGeometriesOverlap";
    private final String P_COPY_MATCHING            = "CopyMatchingFeatures";
    private final String P_COPY_NOT_MATCHING        = "CopyNotMatchingFeatures";
    private final String P_DISPLAY_LINKS            = "DisplayLinks";

    private final String P_USE_ATTRIBUTES           = "UseAttributes";
    private final String P_SRC_ATTRIBUTE            = "SourceAttribute";
    private final String P_SRC_ATTRIBUTE_PREPROCESS = "SourceAttributePreprocess";
    private final String P_TGT_ATTRIBUTE            = "TargetAttribute";
    private final String P_TGT_ATTRIBUTE_PREPROCESS = "TargetAttributePreprocess";
    private final String P_ATTRIBUTE_MATCHER        = "AttributeMatcher";
    private final String P_MAX_STRING_DISTANCE      = "MaximumStringDistance";
    private final String P_HAS_MAX_STRING_DISTANCE  = "HasMaxStringDistance";
    private final String P_MIN_STRING_OVERLAP       = "MinStringOverlap";
    private final String P_HAS_MIN_STRING_OVERLAP   = "HasMinStringOverlap";


    private final String P_TRANSFER_ATTRIBUTES      = "TransferAttributes";
    private final String P_TRANSFER_BEST_MATCH_ONLY = "TransferBestMatchOnly";
    private final String P_STRING_AGGREGATOR        = "StringAggregator";
    private final String P_INTEGER_AGGREGATOR       = "IntegerAggregator";
    private final String P_LONG_AGGREGATOR          = "LongAggregator";
    private final String P_DOUBLE_AGGREGATOR        = "DoubleAggregator";
    private final String P_DATE_AGGREGATOR          = "DateAggregator";
    private final String P_BOOLEAN_AGGREGATOR       = "BooleanAggregator";

    
    private final String MATCHING                     = i18n.get("Matching");
    private final String MATCHING_OPTIONS             = i18n.get("Matching-options");
    
    // Source layer
    private final String SOURCE_LAYER                 = i18n.get("Source-layer");
    private final String SOURCE_LAYER_TOOLTIP         = i18n.get("Source-layer-tooltip");
    private final String SINGLE_SOURCE                = i18n.get("Single-source");
    private final String SINGLE_SOURCE_TOOLTIP        = i18n.get("Single-source-tooltip");
    
    // Target layer
    private final String TARGET_LAYER                 = i18n.get("Target-layer");
    private final String TARGET_LAYER_TOOLTIP         = i18n.get("Target-layer-tooltip");
    private final String SINGLE_TARGET                = i18n.get("Single-target");
    private final String SINGLE_TARGET_TOOLTIP        = i18n.get("Single-target-tooltip");
    
    // Geometry matcher
    private final String GEOMETRIC_OPTIONS            = i18n.get("Geometric-options");
    private final String GEOMETRY_MATCHER             = i18n.get("Geometry-matcher");
    private final String MAXIMUM_DISTANCE             = i18n.get("Maximum-distance");
    private final String MINIMUM_OVERLAPPING          = i18n.get("Minimum-overlapping");
    
    // Output options
    private final String OUTPUT_OPTIONS               = i18n.get("Output-options");
    private final String COPY_MATCHING_FEATURES       = i18n.get("Copy-matching-features");
    private final String COPY_NOT_MATCHING_FEATURES   = i18n.get("Copy-not-matching-features");
    private final String DISPLAY_LINKS                = i18n.get("Display-links");
    
    // Attributes options
    private final String ATTRIBUTE_OPTIONS            = i18n.get("Attribute-options");
    private final String USE_ATTRIBUTES               = i18n.get("Use-attributes");
    private final String SOURCE_LAYER_ATTRIBUTE       = i18n.get("Source-layer-attribute");
    private final String SOURCE_ATT_PREPROCESSING     = i18n.get("Source-att-preprocessing");
    private final String TARGET_LAYER_ATTRIBUTE       = i18n.get("Target-layer-attribute");
    private final String TARGET_ATT_PREPROCESSING     = i18n.get("Target-att-preprocessing");
    private final String ATTRIBUTE_MATCHER            = i18n.get("Attribute-matcher");
    private final String MAXIMUM_STRING_DISTANCE      = i18n.get("Maximum-string-distance");
    private final String MINIMUM_STRING_OVERLAPPING   = i18n.get("Minimum-string-overlapping");
    
    // Attribute transfer / aggregation
    private final String TRANSFER_OPTIONS             = i18n.get("Transfer-options");
    private final String TRANSFER_TO_REFERENCE_LAYER  = i18n.get("Transfer-to-reference-layer");
    private final String TRANSFER_BEST_MATCH_ONLY     = i18n.get("Transfer-best-match-only");
    
    private final String STRING_AGGREGATION           = i18n.get("String-aggregation");
    private final String INTEGER_AGGREGATION          = i18n.get("Integer-aggregation");
    private final String LONG_AGGREGATION             = i18n.get("Long-aggregation");
    private final String DOUBLE_AGGREGATION           = i18n.get("Double-aggregation");
    private final String DATE_AGGREGATION             = i18n.get("Date-aggregation");
    private final String BOOLEAN_AGGREGATION          = i18n.get("Boolean-aggregation");

    // Processing and Error messages
    private final String SEARCHING_MATCHES            = i18n.get("Searching-matches");
    private final String MISSING_INPUT_LAYER          = i18n.get("Missing-input-layer");
    private final String CHOOSE_MATCHER               = i18n.get("Choose-geometry-or-attribute-matcher");

    // Parameters : source layer and cardinality
    private String source_layer_name;
    private boolean single_source = false;
    // Parameters : target layer and cardinality
    private String target_layer_name;
    private boolean single_target = false;

    // Parameters : geometry parameters
    private GeometryMatcher geometry_matcher = CentroidDistanceMatcher.instance();
    private double max_distance = geometry_matcher.getMaximumDistance();
    //private final boolean set_max_distance = !Double.isNaN(max_distance);
    private double min_overlapping = geometry_matcher.getMinimumOverlapping();
    //private final boolean set_min_overlapping = !Double.isNaN(min_overlapping);

    // Parameters : output options
    private boolean copy_matching_features = true;
    private boolean copy_not_matching_features;
    private boolean display_links = false;

    // Parameters : attribute parameters
    private boolean use_attributes = false;
    private String source_att_preprocessing = "";
    private String source_layer_attribute;
    private String target_att_preprocessing = "";
    private String target_layer_attribute;
    private StringMatcher attribute_matcher = 
        StringEqualityIgnoreCaseAndAccentMatcher.instance();
    private double max_string_distance = attribute_matcher.getMaximumDistance();
    private boolean has_max_string_distance = !Double.isNaN(max_string_distance);
    private double min_string_overlapping = attribute_matcher.getMinimumOverlapping();
    private boolean has_min_string_overlapping = !Double.isNaN(min_string_overlapping);

    // Parameters : transfer and aggregation
    private boolean transfer = true;
    private boolean transfer_best_match_only = false;
    // Ignore null by default
    //private boolean ignore_null = true;
    private Aggregator string_aggregator =
            Aggregators.getAggregator(new Aggregators.ConcatenateUnique(true).getName());
    private Aggregator integer_aggregator =
            Aggregators.getAggregator(new Aggregators.IntSum().getName());
    private Aggregator long_aggregator =
            Aggregators.getAggregator(new Aggregators.LongSum().getName());
    private Aggregator double_aggregator =
            Aggregators.getAggregator(new Aggregators.DoubleMean(true).getName());
    private Aggregator date_aggregator =
            Aggregators.getAggregator(new Aggregators.DateMean(true).getName());
    private Aggregator boolean_aggregator =
            Aggregators.getAggregator(new Aggregators.BooleanMajority(true).getName());

    // initialisation of parameters
    {
        addParameter(P_SRC_LAYER, source_layer_name);
        addParameter(P_SINGLE_SRC, single_source);
        addParameter(P_TGT_LAYER, target_layer_name);
        addParameter(P_SINGLE_TGT, single_target);
        addParameter(P_GEOMETRY_MATCHER, geometry_matcher.getClass().getSimpleName());
        addParameter(P_MAX_GEOM_DISTANCE, max_distance);
        addParameter(P_MIN_GEOM_OVERLAP, min_overlapping);
        addParameter(P_COPY_MATCHING, copy_matching_features);
        addParameter(P_COPY_NOT_MATCHING, copy_not_matching_features);
        addParameter(P_DISPLAY_LINKS, display_links);

        addParameter(P_USE_ATTRIBUTES, use_attributes);
        addParameter(P_SRC_ATTRIBUTE, source_layer_attribute);
        addParameter(P_SRC_ATTRIBUTE_PREPROCESS, source_att_preprocessing);
        addParameter(P_TGT_ATTRIBUTE, target_layer_attribute);
        addParameter(P_TGT_ATTRIBUTE_PREPROCESS, target_att_preprocessing);
        addParameter(P_ATTRIBUTE_MATCHER, attribute_matcher.getClass().getSimpleName());
        addParameter(P_MAX_STRING_DISTANCE, max_string_distance);
        addParameter(P_HAS_MAX_STRING_DISTANCE, has_max_string_distance);
        addParameter(P_MIN_STRING_OVERLAP, min_string_overlapping);
        addParameter(P_HAS_MIN_STRING_OVERLAP, has_min_string_overlapping);

        addParameter(P_TRANSFER_ATTRIBUTES, transfer);
        addParameter(P_TRANSFER_BEST_MATCH_ONLY, transfer_best_match_only);

        addParameter(P_STRING_AGGREGATOR,  string_aggregator.getName());
        addParameter(P_INTEGER_AGGREGATOR, integer_aggregator.getName());
        addParameter(P_LONG_AGGREGATOR,    long_aggregator.getName());
        addParameter(P_DOUBLE_AGGREGATOR,  double_aggregator.getName());
        addParameter(P_DATE_AGGREGATOR,    date_aggregator.getName());
        addParameter(P_BOOLEAN_AGGREGATOR, boolean_aggregator.getName());
    }

    public MatchingPlugIn() {
    }
    
    public String getName() {
        return MATCHING;
    }

    public void initialize(PlugInContext context) {
        
        context.getFeatureInstaller().addMainMenuPlugin(
          this, new String[]{MenuNames.PLUGINS, MATCHING},
          MATCHING + "...",
          false, null, new MultiEnableCheck()
          .add(context.getCheckFactory().createTaskWindowMustBeActiveCheck())
          .add(context.getCheckFactory().createAtLeastNLayersMustExistCheck(1)));
    }

    /**
     * Execute method initialize the plugin interface and get all the
     * parameters from the user.
     */
    public boolean execute(PlugInContext context) throws RuleNotFoundException {
        
        try {
            RuleRegistry.loadRules(
                context.getWorkbenchContext().getWorkbench().getPlugInManager().getPlugInDirectory().getPath() + "\\Rules"
            );
        } catch (IllegalArgumentException | IOException | RuleFormatException iae) {
            Logger.warn(iae.getMessage());
            context.getWorkbenchFrame().warnUser(i18n.get("Missing-directory",
                context.getWorkbenchContext().getWorkbench()
                    .getPlugInManager().getPlugInDirectory().getName() +
                    "/Rules"
            ));
        }

        ////////////////////////////////////////////////////////////////////////
        // UI : CREATE MULTITAB INPUT DIALOG
        ////////////////////////////////////////////////////////////////////////
                
        final MultiTabInputDialog dialog = new MultiTabInputDialog(
            context.getWorkbenchFrame(), MATCHING_OPTIONS, GEOMETRIC_OPTIONS, true);
        initDialog(dialog, context);

        GUIUtil.centreOnWindow(dialog);
        dialog.setVisible(true);

        if (dialog.wasOKPressed()) {

            // Get source layer parameters
            Layer source_layer = dialog.getLayer(SOURCE_LAYER);
            source_layer_name  = source_layer.getName();
            single_source      = dialog.getBoolean(SINGLE_SOURCE);

            // Get target layer parameters
            Layer target_layer = dialog.getLayer(TARGET_LAYER);
            target_layer_name  = target_layer.getName();
            single_target      = dialog.getBoolean(SINGLE_TARGET);

            // Get geometry matcher and set its parameters
            geometry_matcher   = (GeometryMatcher)dialog.getValue(GEOMETRY_MATCHER);
            max_distance       = dialog.getDouble(MAXIMUM_DISTANCE);
            min_overlapping    = dialog.getDouble(MINIMUM_OVERLAPPING);
            geometry_matcher.setMaximumDistance(max_distance);
            geometry_matcher.setMinimumOverlapping(min_overlapping);
            
            // Get output options
            copy_matching_features       = dialog.getBoolean(COPY_MATCHING_FEATURES);
            copy_not_matching_features   = dialog.getBoolean(COPY_NOT_MATCHING_FEATURES);
            display_links                = dialog.getBoolean(DISPLAY_LINKS);
            
            // get attribute options
            use_attributes               = dialog.getBoolean(USE_ATTRIBUTES);
            source_layer_attribute       = dialog.getText(SOURCE_LAYER_ATTRIBUTE);
            source_att_preprocessing     = dialog.getText(SOURCE_ATT_PREPROCESSING);
            target_layer_attribute       = dialog.getText(TARGET_LAYER_ATTRIBUTE);
            target_att_preprocessing     = dialog.getText(TARGET_ATT_PREPROCESSING);
            attribute_matcher            = (StringMatcher)dialog.getValue(ATTRIBUTE_MATCHER);
            max_string_distance          = dialog.getDouble(MAXIMUM_STRING_DISTANCE);
            min_string_overlapping       = dialog.getDouble(MINIMUM_STRING_OVERLAPPING);
            if (!use_attributes) attribute_matcher = MatchAllStringsMatcher.MATCH_ALL;
            else attribute_matcher.setAttributes(source_layer_attribute, 
                                                 target_layer_attribute);
            attribute_matcher.setMaximumDistance(max_string_distance);
            attribute_matcher.setMinimumOverlapping(min_string_overlapping);
            attribute_matcher.setSourceRule(RuleRegistry.getRule(source_att_preprocessing));
            attribute_matcher.setTargetRule(RuleRegistry.getRule(target_att_preprocessing));
            
            // get transfer options
            transfer                 = dialog.getBoolean(TRANSFER_TO_REFERENCE_LAYER);
            transfer_best_match_only = dialog.getBoolean(TRANSFER_BEST_MATCH_ONLY);
            string_aggregator        = (Aggregator)dialog.getComboBox(STRING_AGGREGATION).getSelectedItem();
            integer_aggregator       = (Aggregator)dialog.getComboBox(INTEGER_AGGREGATION).getSelectedItem();
            long_aggregator          = (Aggregator)dialog.getComboBox(LONG_AGGREGATION).getSelectedItem();
            double_aggregator        = (Aggregator)dialog.getComboBox(DOUBLE_AGGREGATION).getSelectedItem();
            date_aggregator          = (Aggregator)dialog.getComboBox(DATE_AGGREGATION).getSelectedItem();
            boolean_aggregator       = (Aggregator)dialog.getComboBox(BOOLEAN_AGGREGATION).getSelectedItem();

            System.out.println("start adding parameters");

            addParameter(P_SRC_LAYER, source_layer_name);
            addParameter(P_SINGLE_SRC, single_source);
            addParameter(P_TGT_LAYER, target_layer_name);
            addParameter(P_SINGLE_TGT, single_target);
            addParameter(P_GEOMETRY_MATCHER, geometry_matcher.getClass().getSimpleName());
            addParameter(P_MAX_GEOM_DISTANCE, max_distance);
            addParameter(P_MIN_GEOM_OVERLAP, min_overlapping);
            addParameter(P_COPY_MATCHING, copy_matching_features);
            addParameter(P_COPY_NOT_MATCHING, copy_not_matching_features);
            addParameter(P_DISPLAY_LINKS, display_links);

            addParameter(P_USE_ATTRIBUTES, use_attributes);
            addParameter(P_SRC_ATTRIBUTE, source_layer_attribute);
            addParameter(P_SRC_ATTRIBUTE_PREPROCESS, source_att_preprocessing);
            addParameter(P_TGT_ATTRIBUTE, target_layer_attribute);
            addParameter(P_TGT_ATTRIBUTE_PREPROCESS, target_att_preprocessing);
            addParameter(P_ATTRIBUTE_MATCHER, attribute_matcher.getClass().getSimpleName());
            addParameter(P_MAX_STRING_DISTANCE, max_string_distance);
            addParameter(P_HAS_MAX_STRING_DISTANCE, has_max_string_distance);
            addParameter(P_MIN_STRING_OVERLAP, min_string_overlapping);
            addParameter(P_HAS_MIN_STRING_OVERLAP, has_min_string_overlapping);

            addParameter(P_TRANSFER_ATTRIBUTES, transfer);
            addParameter(P_TRANSFER_BEST_MATCH_ONLY, transfer_best_match_only);
            addParameter(P_STRING_AGGREGATOR, string_aggregator.getName());
            addParameter(P_INTEGER_AGGREGATOR, integer_aggregator.getName());
            addParameter(P_LONG_AGGREGATOR, long_aggregator.getName());
            addParameter(P_DOUBLE_AGGREGATOR, double_aggregator.getName());
            addParameter(P_DATE_AGGREGATOR, date_aggregator.getName());
            addParameter(P_BOOLEAN_AGGREGATOR, boolean_aggregator.getName());

            if ((geometry_matcher instanceof MatchAllMatcher) && !use_attributes) {
                context.getWorkbenchFrame().warnUser(CHOOSE_MATCHER);
                return false;
            }
            return true;
        }
        else return false;
        
    }
    
    private void initDialog(final MultiTabInputDialog dialog, final PlugInContext context) {
        
        ////////////////////////////////////////////////////////////////////////
        // UI : INITIALIZE LAYERS FROM LAST ONES OR FROM CONTEXT
        ////////////////////////////////////////////////////////////////////////

        Layer source_layer;
        Layer target_layer;
        source_layer = context.getLayerManager().getLayer(source_layer_name);
        if (source_layer == null) source_layer = context.getCandidateLayer(0);
        
        target_layer = context.getLayerManager().getLayer(target_layer_name);
        int layerNumber = context.getLayerManager().getLayers().size();
        if (target_layer == null) target_layer = context.getCandidateLayer(layerNumber>1?1:0);
        
        ////////////////////////////////////////////////////////////////////////
        // UI : CHOOSE SOURCE LAYER AND SOURCE CARDINALITY
        ////////////////////////////////////////////////////////////////////////

        dialog.addLabel("<html><b>"+GEOMETRIC_OPTIONS+"</b></html>");
        
        final JComboBox<Layer> jcb_layer = dialog.addLayerComboBox(SOURCE_LAYER,
            source_layer, SOURCE_LAYER_TOOLTIP, context.getLayerManager());
        jcb_layer.setPreferredSize(new Dimension(220,20));
        jcb_layer.addActionListener(e -> updateDialog(dialog));
        dialog.addCheckBox(SINGLE_SOURCE, single_source, SINGLE_SOURCE_TOOLTIP);

        ////////////////////////////////////////////////////////////////////////
        // UI : CHOOSE GEOMETRY MATCHER
        ////////////////////////////////////////////////////////////////////////
        Collection<GeometryMatcher> geomMatcherList =
                MatcherRegistry.GEOMETRY_MATCHERS.getMap().values();
        final JComboBox<GeometryMatcher> jcb_geom_operation =
                dialog.addComboBox(GEOMETRY_MATCHER, geometry_matcher, geomMatcherList, null);

        final JTextField jtf_dist = dialog.addDoubleField(MAXIMUM_DISTANCE, max_distance, 12, null);
        jtf_dist.setEnabled(!Double.isNaN(geometry_matcher.getMaximumDistance()));
        
        final JTextField jtf_overlap = dialog.addDoubleField(MINIMUM_OVERLAPPING, min_overlapping, 12, null);
        jtf_overlap.setEnabled(!Double.isNaN(geometry_matcher.getMinimumOverlapping()));

        ////////////////////////////////////////////////////////////////////////
        // UI : CHOOSE TARGET LAYER AND SOURCE CARDINALITY
        ////////////////////////////////////////////////////////////////////////
        
        final JComboBox<Layer> jcb_layer_tgt = dialog.addLayerComboBox(TARGET_LAYER,
            target_layer, TARGET_LAYER_TOOLTIP, context.getLayerManager());
        jcb_layer_tgt.setPreferredSize(new Dimension(220,20));
        jcb_layer_tgt.addActionListener(e -> updateDialog(dialog));
        dialog.addCheckBox(SINGLE_TARGET, single_target, SINGLE_TARGET_TOOLTIP);

        jcb_geom_operation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDialog(dialog);
                // Set jtf_dist and jtf_overlap to the last used values for this matcher
                geometry_matcher = (GeometryMatcher)jcb_geom_operation.getSelectedItem();
                if (geometry_matcher != null) {
                    jtf_dist.setText("" + geometry_matcher.getMaximumDistance());
                    jtf_overlap.setText("" + geometry_matcher.getMinimumOverlapping());
                } else {
                    Logger.warn("MatchingPlugin : null geometry_matcher");
                }
            }
        });
        jtf_dist.addActionListener(e ->
            geometry_matcher.setMaximumDistance(dialog.getDouble(MAXIMUM_DISTANCE)));
        jtf_overlap.addActionListener(e ->
            geometry_matcher.setMinimumOverlapping(dialog.getDouble(MINIMUM_OVERLAPPING)));

        ////////////////////////////////////////////////////////////////////////
        // UI : CHOOSE OUTPUT OPTIONS
        ////////////////////////////////////////////////////////////////////////
        dialog.addSeparator();
        dialog.addLabel("<html><b>"+OUTPUT_OPTIONS+"</b></html>");

        dialog.addCheckBox(COPY_MATCHING_FEATURES, copy_matching_features, null);
        dialog.addCheckBox(COPY_NOT_MATCHING_FEATURES, copy_not_matching_features, null);
        dialog.addCheckBox(DISPLAY_LINKS, display_links, null);

        ////////////////////////////////////////////////////////////////////////
        // UI : CHOOSE ATTRIBUTE OPTIONS
        ////////////////////////////////////////////////////////////////////////
        dialog.addPane(ATTRIBUTE_OPTIONS);

        final JCheckBox jcb_use_attributes = dialog.addCheckBox(USE_ATTRIBUTES, use_attributes, null);
        
        final JComboBox<String> jcb_src_att_preprocessing = dialog.addComboBox(SOURCE_ATT_PREPROCESSING,
            source_att_preprocessing, Arrays.asList(RuleRegistry.getRules()), null);
        final JComboBox<String> jcb_src_attribute = dialog.addAttributeComboBox(
            SOURCE_LAYER_ATTRIBUTE, SOURCE_LAYER, AttributeTypeFilter.STRING_FILTER, null);
        jcb_src_attribute.setSelectedItem(source_layer_attribute);
        jcb_src_attribute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (jcb_src_attribute.getSelectedItem() != null) {
                    source_layer_attribute = jcb_src_attribute.getSelectedItem().toString();
                }
            }
        });
        
        
        // Initialize string matching options
        Collection<StringMatcher> stringMatcherList =
                MatcherRegistry.STRING_MATCHERS.getMap().values();
        final JComboBox<StringMatcher> jcb_attr_operation = dialog.addComboBox(
            ATTRIBUTE_MATCHER, attribute_matcher, stringMatcherList, null);
        
        final JTextField jtf_string_dist = dialog.addDoubleField(MAXIMUM_STRING_DISTANCE, max_string_distance, 12, null);
        jtf_string_dist.setEnabled(has_max_string_distance);
        
        final JTextField jtf_string_overlap = dialog.addDoubleField(MINIMUM_STRING_OVERLAPPING, min_string_overlapping, 12, null);
        jtf_string_overlap.setEnabled(has_min_string_overlapping);
        
        final JComboBox<String> jcb_tgt_att_preprocessing = dialog.addComboBox(TARGET_ATT_PREPROCESSING,
            target_att_preprocessing, Arrays.asList(RuleRegistry.getRules()), null);
        final JComboBox<String> jcb_tgt_attribute = dialog.addAttributeComboBox(
            TARGET_LAYER_ATTRIBUTE, TARGET_LAYER, AttributeTypeFilter.STRING_FILTER, null);
        jcb_tgt_attribute.setSelectedItem(target_layer_attribute);
        jcb_tgt_attribute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (jcb_tgt_attribute.getSelectedItem() != null) {
                    target_layer_attribute = jcb_tgt_attribute.getSelectedItem().toString();
                }
            }
        });
        
        jcb_attr_operation.addActionListener(e -> updateDialog(dialog));
        jcb_use_attributes.addActionListener(e -> updateDialog(dialog));

        ////////////////////////////////////////////////////////////////////////
        // UI : TRANSFER ATTRIBUTE / AGGREGATION
        ////////////////////////////////////////////////////////////////////////
        dialog.addSeparator();
        dialog.addPane(TRANSFER_OPTIONS);
        dialog.addSubTitle(TRANSFER_OPTIONS);
        
        final JCheckBox jcb_transfer =
            dialog.addCheckBox(TRANSFER_TO_REFERENCE_LAYER, transfer, null);
        jcb_transfer.addActionListener(e -> updateDialog(dialog));
        final JCheckBox jcb_transfer_best_match_only =
            dialog.addCheckBox(TRANSFER_BEST_MATCH_ONLY, transfer_best_match_only, null);
        jcb_transfer_best_match_only.addActionListener(e -> updateDialog(dialog));
        
        dialog.addComboBox(
                STRING_AGGREGATION, string_aggregator,
                Aggregators.getAggregators(AttributeType.STRING).values(), null
        );
        dialog.addComboBox(
                INTEGER_AGGREGATION, integer_aggregator,
                Aggregators.getAggregators(AttributeType.INTEGER).values(), null
        );
        dialog.addComboBox(
                LONG_AGGREGATION, long_aggregator,
                Aggregators.getAggregators(AttributeType.LONG).values(), null
        );
        dialog.addComboBox(
                DOUBLE_AGGREGATION, double_aggregator,
                Aggregators.getAggregators(AttributeType.DOUBLE).values(), null
        );
        dialog.addComboBox(
                DATE_AGGREGATION, date_aggregator,
                Aggregators.getAggregators(AttributeType.DATE).values(), null
        );
        dialog.addComboBox(
                BOOLEAN_AGGREGATION, boolean_aggregator,
                Aggregators.getAggregators(AttributeType.BOOLEAN).values(), null
        );
        
        updateDialog(dialog);
    }
    
    
    // update dialog is called by several component listeners to update the 
    // dialog before the final validation
    // 2012-06-29 : component values are stored in local variables as this is 
    // not necessary to change the plugin parameters until final validation
    private void updateDialog(MultiTabInputDialog dialog) {

        // Updates related to a geometry_matcher change
        geometry_matcher = (GeometryMatcher)dialog.getValue(GEOMETRY_MATCHER);
        dialog.setFieldEnabled(MAXIMUM_DISTANCE, !Double.isNaN(geometry_matcher.getMaximumDistance()));
        dialog.setFieldEnabled(MINIMUM_OVERLAPPING, !Double.isNaN(geometry_matcher.getMinimumOverlapping()));

        // Updates related to a layer change
        Layer srcLayer      = dialog.getLayer(SOURCE_LAYER);
        Layer tgtLayer      = dialog.getLayer(TARGET_LAYER);
        boolean srcLayer_has_attributes = 
            srcLayer.getFeatureCollectionWrapper().getFeatureSchema().getAttributeCount() > 1;
        boolean srcLayer_has_string_attributes = 
            AttributeTypeFilter.STRING_FILTER.filter(srcLayer.getFeatureCollectionWrapper().getFeatureSchema()).size() > 0;
        boolean tgtLayer_has_string_attributes = 
            AttributeTypeFilter.STRING_FILTER.filter(tgtLayer.getFeatureCollectionWrapper().getFeatureSchema()).size() > 0;
        dialog.setFieldEnabled(USE_ATTRIBUTES, srcLayer_has_string_attributes && 
                                               tgtLayer_has_string_attributes);
        dialog.setTabEnabled(ATTRIBUTE_OPTIONS, srcLayer_has_string_attributes && 
                                                tgtLayer_has_string_attributes);
        if (!srcLayer_has_string_attributes || !tgtLayer_has_string_attributes) {
            attribute_matcher = MatchAllStringsMatcher.MATCH_ALL;
        } else {
            attribute_matcher = (StringMatcher)dialog.getValue(ATTRIBUTE_MATCHER);
        }
        
        // Updates related to attribute transfer
        dialog.setTabEnabled(TRANSFER_OPTIONS, srcLayer_has_attributes);

        boolean _transfer = dialog.getBoolean(TRANSFER_TO_REFERENCE_LAYER);
        dialog.setFieldEnabled(TRANSFER_BEST_MATCH_ONLY, _transfer);

        boolean _transfer_best_match_only = dialog.getBoolean(TRANSFER_BEST_MATCH_ONLY);
        dialog.setFieldEnabled(STRING_AGGREGATION, _transfer && !_transfer_best_match_only);
        dialog.setFieldEnabled(INTEGER_AGGREGATION, _transfer && !_transfer_best_match_only);
        dialog.setFieldEnabled(LONG_AGGREGATION, _transfer && !_transfer_best_match_only);
        dialog.setFieldEnabled(DOUBLE_AGGREGATION, _transfer && !_transfer_best_match_only);
        dialog.setFieldEnabled(DATE_AGGREGATION, _transfer && !_transfer_best_match_only);
        dialog.setFieldEnabled(BOOLEAN_AGGREGATION, _transfer && !_transfer_best_match_only);
        
        // Updates related to attribute matching
        boolean _use_attributes = dialog.getBoolean(USE_ATTRIBUTES);
        dialog.setFieldEnabled(SOURCE_LAYER_ATTRIBUTE, _use_attributes);
        dialog.setFieldEnabled(SOURCE_ATT_PREPROCESSING, _use_attributes);
        dialog.setFieldEnabled(TARGET_LAYER_ATTRIBUTE, _use_attributes);
        dialog.setFieldEnabled(TARGET_ATT_PREPROCESSING, _use_attributes);

        dialog.setFieldEnabled(ATTRIBUTE_MATCHER, _use_attributes);

        dialog.setFieldEnabled(MAXIMUM_STRING_DISTANCE, _use_attributes && 
            (attribute_matcher instanceof LevenshteinDistanceMatcher ||
                    attribute_matcher instanceof DamarauLevenshteinDistanceMatcher));

    }
    
    /**
     * Run executes the main process, looping through matching layer, and
     * looking for candidates in the reference layer.
     */
    public void run(TaskMonitor monitor, PlugInContext context) throws Exception {

        // layers and cardinality constraints
        source_layer_name          = getStringParam(P_SRC_LAYER);
        single_source              = getBooleanParam(P_SINGLE_SRC);
        target_layer_name          = getStringParam(P_TGT_LAYER);
        single_target              = getBooleanParam(P_SINGLE_TGT);

        // geometry matcher
        geometry_matcher           = MatcherRegistry.GEOMETRY_MATCHERS
                .get(getStringParam(P_GEOMETRY_MATCHER));
        if (geometry_matcher == null) {
            throw new Exception("GeometryMatcher '" + getStringParam(P_GEOMETRY_MATCHER) + "' has not been found");
        }
        max_distance               = getDoubleParam(P_MAX_GEOM_DISTANCE);
        min_overlapping            = getDoubleParam(P_MIN_GEOM_OVERLAP);
        copy_matching_features     = getBooleanParam(P_COPY_MATCHING);
        copy_not_matching_features = getBooleanParam(P_COPY_NOT_MATCHING);
        display_links              = getBooleanParam(P_DISPLAY_LINKS);
        // derived
        geometry_matcher.setMaximumDistance(max_distance);
        geometry_matcher.setMinimumOverlapping(min_overlapping);

        // attribute matcher
        use_attributes           = getBooleanParam(P_USE_ATTRIBUTES);
        source_layer_attribute   = getStringParam(P_SRC_ATTRIBUTE);
        source_att_preprocessing = getStringParam(P_SRC_ATTRIBUTE_PREPROCESS);
        target_layer_attribute   = getStringParam(P_TGT_ATTRIBUTE);
        target_att_preprocessing = getStringParam(P_TGT_ATTRIBUTE_PREPROCESS);
        attribute_matcher        = MatcherRegistry.STRING_MATCHERS
                .get(getStringParam(P_ATTRIBUTE_MATCHER));
        max_string_distance      = getDoubleParam(P_MAX_STRING_DISTANCE);
        has_max_string_distance  = getBooleanParam(P_HAS_MAX_STRING_DISTANCE);
        min_string_overlapping   = getDoubleParam(P_MIN_STRING_OVERLAP);
        has_min_string_overlapping = getBooleanParam(P_HAS_MIN_STRING_OVERLAP);
        // derived
        if (!use_attributes) attribute_matcher = MatchAllStringsMatcher.MATCH_ALL;
        else {
            if (attribute_matcher == null) {
                throw new Exception("Attribute Matcher '" + getStringParam(P_ATTRIBUTE_MATCHER) + "' has not been found");
            }
            attribute_matcher.setAttributes(source_layer_attribute, target_layer_attribute);
            attribute_matcher.setMaximumDistance(max_string_distance);
            attribute_matcher.setMinimumOverlapping(min_string_overlapping);
        }

        // transfer options
        transfer                 = getBooleanParam(P_TRANSFER_ATTRIBUTES);
        transfer_best_match_only = getBooleanParam(P_TRANSFER_BEST_MATCH_ONLY);
        string_aggregator        = Aggregators.getAggregator(getStringParam(P_STRING_AGGREGATOR));
        string_aggregator.setIgnoreNull(true);
        integer_aggregator       = Aggregators.getAggregator(getStringParam(P_INTEGER_AGGREGATOR));
        integer_aggregator.setIgnoreNull(true);
        long_aggregator          = Aggregators.getAggregator(getStringParam(P_LONG_AGGREGATOR));
        long_aggregator.setIgnoreNull(true);
        double_aggregator        = Aggregators.getAggregator(getStringParam(P_DOUBLE_AGGREGATOR));
        double_aggregator.setIgnoreNull(true);
        date_aggregator          = Aggregators.getAggregator(getStringParam(P_DATE_AGGREGATOR));
        date_aggregator.setIgnoreNull(true);
        boolean_aggregator       = Aggregators.getAggregator(getStringParam(P_BOOLEAN_AGGREGATOR));
        boolean_aggregator.setIgnoreNull(true);

        
        Layer source_layer = context.getLayerManager().getLayer(source_layer_name);
        Layer target_layer = context.getLayerManager().getLayer(target_layer_name);
        if (source_layer == null || target_layer ==  null) {
            context.getWorkbenchFrame().warnUser(MISSING_INPUT_LAYER);
            return;
        }
        FeatureCollection source_fc = source_layer.getFeatureCollectionWrapper();
        FeatureCollection target_fc = target_layer.getFeatureCollectionWrapper();

        monitor.allowCancellationRequests();
        monitor.report(SEARCHING_MATCHES);
        FeatureCollectionMatcher matcher = new FeatureCollectionMatcher(
                source_fc.getFeatures(), target_fc.getFeatures(), 
                geometry_matcher, attribute_matcher, monitor);
        Collection<Feature> features = matcher.matchAll(single_source, single_target);
        if (matcher.interrupted) return;

        if (copy_matching_features) {
            Layer lyr = createLayer(
                features, context,
                source_layer.getName() + "-" + i18n.get("matched"), true);
            if (lyr != null) setMatchingStyle(lyr);
        }
        if (copy_not_matching_features) {
            Layer lyr = createLayer(
                inverse(source_layer.getFeatureCollectionWrapper(), features), 
                context, 
                source_layer.getName() + "-" + i18n.get("un-matched"), true);
            if (lyr != null) setNotMatchingStyle(lyr);
        }
        if (display_links) {
            Layer lyr = createLayer(createLinks(matcher.getMatchMap()), context,
                i18n.get("Links") + " " + source_layer.getName() + " - " + target_layer.getName(), false);
            if (lyr != null) setLinkStyle(lyr);
        }
        if (transfer) {
            FeatureSchema target_schema = target_fc.getFeatureSchema();
            FeatureSchema new_schema = target_schema.clone();
            if (!new_schema.hasAttribute("X_COUNT")) {
                new_schema.addAttribute("X_COUNT", AttributeType.INTEGER);
            }
            if (!new_schema.hasAttribute("X_MAX_SCORE")) {
                new_schema.addAttribute("X_MAX_SCORE", AttributeType.DOUBLE);
            }
            if (!new_schema.hasAttribute("X_MIN_DISTANCE")) {
                new_schema.addAttribute("X_MIN_DISTANCE", AttributeType.DOUBLE);
            }
            FeatureSchema source_schema = source_fc.getFeatureSchema();
            for (int i = 0 ; i < source_schema.getAttributeCount() ; i++) {
                if (source_schema.getAttributeType(i) != AttributeType.GEOMETRY &&
                    source_schema.getAttributeType(i) != AttributeType.OBJECT) {
                    new_schema.addAttribute(
                        "X_" + source_schema.getAttributeName(i),
                        source_schema.getAttributeType(i));
                }
            }
            FeatureCollection new_dataset = new FeatureDataset(new_schema);
            MatchMap matchMap = matcher.getMatchMap();
            // If user wants to transfer attributes from the best match only
            // and MatchMap has not yet been filtered by single_source option
            if (transfer_best_match_only && !single_source) {
                matchMap = matchMap.filter(true, false);
            }
            for (Feature f : target_fc.getFeatures()) {
                Feature bf = new BasicFeature(new_schema);
                Object[] attributes = new Object[new_schema.getAttributeCount()];
                System.arraycopy(f.getAttributes(), 0, attributes, 0, target_schema.getAttributeCount());
                bf.setAttributes(attributes);
                List<Feature> matches = matchMap.getMatchedFeaturesFromTarget(f);
                bf.setAttribute("X_COUNT", matches.size());
                if (matches.isEmpty()) bf.setAttribute("X_MAX_SCORE", 0.0);
                else bf.setAttribute("X_MAX_SCORE", matchMap.getMatchesForTargetFeature(f).iterator().next().getScore());
                double minDistance = Double.NaN;
                for (Match match : matchMap.getMatchesForTargetFeature(f)) {
                    minDistance = Double.isNaN(minDistance) ? 
                        f.getGeometry().distance(match.getSource().getGeometry()) : 
                        Math.min(minDistance, f.getGeometry().distance(match.getSource().getGeometry()));
                }
                if (!Double.isNaN(minDistance)) bf.setAttribute("X_MIN_DISTANCE", minDistance);
                for (int i = 0 ; i < source_schema.getAttributeCount() ; i++) {
                    String name = source_schema.getAttributeName(i);
                    AttributeType type = source_schema.getAttributeType(i);
                    if (type == AttributeType.GEOMETRY) continue;
                    else if (type == AttributeType.OBJECT) continue;
                    else if (type == AttributeType.STRING) {
                        string_aggregator.reset();
                        for (Feature mf : matches) string_aggregator.addValue(mf.getAttribute(i));
                        bf.setAttribute("X_" + name, string_aggregator.getResult());
                    }
                    else if (type == AttributeType.INTEGER) {
                        integer_aggregator.reset();
                        for (Feature mf : matches) integer_aggregator.addValue(mf.getAttribute(i));
                        bf.setAttribute("X_" + name, integer_aggregator.getResult());
                    }
                    else if (type == AttributeType.LONG) {
                      long_aggregator.reset();
                      for (Feature mf : matches) long_aggregator.addValue(mf.getAttribute(i));
                      bf.setAttribute("X_" + name, long_aggregator.getResult());
                    }
                    else if (type == AttributeType.DOUBLE) {
                        double_aggregator.reset();
                        for (Feature mf : matches) double_aggregator.addValue(mf.getAttribute(i));
                        bf.setAttribute("X_" + name, double_aggregator.getResult());
                    }
                    else if (type == AttributeType.DATE) {
                        date_aggregator.reset();
                        for (Feature mf : matches) date_aggregator.addValue(mf.getAttribute(i));
                        bf.setAttribute("X_" + name, date_aggregator.getResult());
                    }
                    else if (type == AttributeType.BOOLEAN) {
                      boolean_aggregator.reset();
                      for (Feature mf : matches) boolean_aggregator.addValue(mf.getAttribute(i));
                      bf.setAttribute("X_" + name, boolean_aggregator.getResult());
                    }
                }
                new_dataset.add(bf);
            }
            createLayer(new_dataset.getFeatures(), context, target_layer.getName(), false);
        }
    }
    
    private Layer createLayer(Collection<Feature> features, PlugInContext context, String name, boolean clone) {
        if (features.size()>0) {
            FeatureSchema schema = (features.iterator().next()).getSchema();
            FeatureCollection fc = new FeatureDataset(schema);
            if (clone) {
                for (Feature f : features) {
                    fc.add(f.clone(false));
                }
            }
            else fc.addAll(features);
            return context.getLayerManager()
                          .addLayer(StandardCategoryNames.RESULT, name, fc);
        }
        return null;
    }
    
    private Collection<Feature> inverse(FeatureCollection fc, 
                                        Collection<Feature> features) {
        Map<Integer,Feature> map = new HashMap<>();
        for (Feature f : features) map.put(f.getID(), f);
        List<Feature> inverse = new ArrayList<>();
        for (Feature feature : fc.getFeatures()) {
            if (!map.containsKey((feature).getID())) inverse.add(feature);
        }
        return inverse;
    }
    
    public Collection<Feature> createLinks(MatchMap map) {
        List<Feature> links = new ArrayList<>();
        GeometryFactory gf = new GeometryFactory();
        FeatureSchema schema = new FeatureSchema();
        schema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
        schema.addAttribute("SOURCE", AttributeType.INTEGER);
        schema.addAttribute("TARGET", AttributeType.INTEGER);
        schema.addAttribute("SCORE", AttributeType.DOUBLE);
        for (Match match : map.getAllMatches()) {
            BasicFeature f = new BasicFeature(schema);
            Coordinate[] coords = new Coordinate[2];
            // [2013-04-21] cannot draw link ifa geometry is empty 
            if (match.getSource().getGeometry().isEmpty() || 
                match.getTarget().getGeometry().isEmpty()) continue;
            if (geometry_matcher instanceof MinimumDistanceMatcher) {
                coords = DistanceOp.nearestPoints(
                    match.getSource().getGeometry(), 
                    match.getTarget().getGeometry());
            } else {
                coords[0] = match.getSource().getGeometry().getInteriorPoint().getCoordinate();
                coords[1] = match.getTarget().getGeometry().getInteriorPoint().getCoordinate();
            }
            Geometry g = coords[0].equals(coords[1]) ? 
                         gf.createPoint(coords[0]):
                         gf.createLineString(coords);
            f.setGeometry(g);
            f.setAttribute("SOURCE", match.getSource().getID());
            f.setAttribute("TARGET", match.getTarget().getID());
            f.setAttribute("SCORE",  match.getScore());
            links.add(f);
        }
        return links;
    }
    
    public void setMatchingStyle(Layer layer) {
        BasicStyle style = layer.getBasicStyle();
        style.setLineColor(Color.GREEN);
        style.setLineWidth(5);
        style.setAlpha(200);
        style.setFillColor(Color.LIGHT_GRAY);
    }
    
    public void setNotMatchingStyle(Layer layer) {
        BasicStyle style = layer.getBasicStyle();
        style.setLineColor(Color.ORANGE);
        style.setLineWidth(5);
        style.setAlpha(200);
        style.setFillColor(Color.LIGHT_GRAY);
    }
    
    public void setLinkStyle(Layer layer) {
        BasicStyle style = layer.getBasicStyle();
        style.setLineColor(Color.RED);
        style.setLineWidth(2);
        style.setAlpha(255);
        style.setRenderingFill(false);
        layer.addStyle(new MyRingVertexStyle());
        layer.getStyle(MyRingVertexStyle.class).setEnabled(true);
    }
    
    private static class MyRingVertexStyle extends RingVertexStyle {
    
        MyRingVertexStyle() {super();}
    
        public int getSize() {return 25;}
        
        public void paint(Feature f, Graphics2D g, Viewport viewport) throws Exception {
            if (f.getGeometry() instanceof Point) {
                Coordinate coord = f.getGeometry().getCoordinate();
                paint(g, viewport.toViewPoint(new Point2D.Double(coord.x, coord.y)));
            }
        }
    
        protected void render(java.awt.Graphics2D g) {
            g.setStroke(new java.awt.BasicStroke(2.5f));
            g.setColor(Color.RED);
            g.draw(shape);
        }
    }

}
