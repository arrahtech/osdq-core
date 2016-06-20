/*
 * OutputDataExporter.java
 *
 * Created: 2006-05-23
 *
 * Java Data Mining Framework (http://jdmf.sourceforge.net)
 * Copyright (C) 2006  Janusz Marchewa
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * $Author: quorthon $
 * $LastChangedRevision: 3 $
 * $LastChangedDate: 2006-07-02 21:52:16 +0200 (nie, 02 lip 2006) $
 */
package net.sf.jdmf.visualization.export;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

/**
 * Export the data mining model to other formats. Useful for GUIs using this 
 * library.
 * 
 * @author quorthon
 */
public class OutputDataExporter {
    /**
     * Exports charts created using JFreeChart to SVG (which can be embedded
     * in web pages or anywhere else).
     * 
     * @param chart the chart being exported
     * @param width the width of the SVG
     * @param height the height of the SVG
     * @return SVG document
     * 
     * @see net.sf.jdmf.visualization.clustering.ChartGenerator
     */
    public SVGDocument exportChartToSVG( JFreeChart chart, Integer width, 
            Integer height ) {
        DOMImplementation domImplementation 
            = SVGDOMImplementation.getDOMImplementation();
        
        String svgNamespace = SVGDOMImplementation.SVG_NAMESPACE_URI;
        
        SVGDocument svgDocument 
            = (SVGDocument) domImplementation.createDocument( 
                svgNamespace, "svg", null );
        
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D( svgDocument );
        
        Rectangle2D rectangle2D = new Rectangle2D.Double( 0, 0, width, height );
        
        chart.draw( svgGraphics2D, rectangle2D );
        
        svgGraphics2D.dispose();
        
        svgGraphics2D.setSVGCanvasSize( new Dimension( width, height ) );
        Element rootElement = svgDocument.getDocumentElement();
        
        svgGraphics2D.getRoot( rootElement );
        
        return svgDocument;
    }
}
