package org.arrah.gui.swing;

/***********************************************
 *     Copyright to Arrah Technology 2015      *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 *                                             *
 ***********************************************/

/*
 * This class is used for creating Regression 
 * Plot
 *
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.arrah.framework.ndtable.ReportTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.function.PolynomialFunction2D;
import org.jfree.data.function.PowerFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class RegressionPlotPanel extends JPanel implements ActionListener, Serializable {
	private static final long serialVersionUID = 1L;
	private String xTitle, yTitle;
	private String title;
	private XYSeries  dataset;

	public RegressionPlotPanel(String titleName, String xName, String yName) {
		title = titleName;
		xTitle = xName;
		yTitle = yName;
		dataset = new XYSeries(title);
		addMouseListener(new PopupListener());
	}
	
	public void addRTMDataSet(ReportTableModel rtm, String xcol1, String ycol2) throws Exception {
		int rowC= rtm.getModel().getRowCount();		
		int index = rtm.getColumnIndex(xcol1);
		int comIndex = rtm.getColumnIndex(ycol2);
		
		for (int i=0; i < rowC; i++) {
			try {
				Object xcell = rtm.getModel().getValueAt(i, index);
				Object ycell = rtm.getModel().getValueAt(i, comIndex);
				dataset.add(new Double(xcell.toString()) ,new Double(ycell.toString()));
				
			} catch (Exception e) {
				ConsoleFrame.addText("\n Exception for row :" +i + "  Execption:"+e.getLocalizedMessage());
			}
		}
	}
	// Create the Time Series Plot
	public void drawRegressionPlot(int dimIndex) throws Exception {
		
		
		NumberAxis numberaxis = new NumberAxis(xTitle);
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis(yTitle);
        numberaxis1.setAutoRangeIncludesZero(false);
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(false, true);
        XYSeriesCollection xyseriescollection = new XYSeriesCollection(dataset);
        XYPlot xyplot = new XYPlot(xyseriescollection, numberaxis, numberaxis1, xylineandshaperenderer);
        double ad[] = null; // to hold constant and slope value
        Function2D functionLine = null; // Function to generate line
        if (dimIndex == 0) {
        	 ad = Regression.getOLSRegression(xyseriescollection, 0);
        	// Add a+bx = a and b to tile
            title = title + "  Const:"+String.format("%.4f",ad[0])+"  Slope:"+String.format("%.4f",ad[1]);
            functionLine  = new LineFunction2D(ad[0], ad[1]);
        } else if (dimIndex == 1) { // Polynomial default order 4 --  a +bx+ cx^2+dx^3 +ex^4
        	ad = Regression.getPolynomialRegression(xyseriescollection, 0,4);
        	title = title + "  Const:"+String.format("%.4f",ad[0])+"  Slope 1:"+String.format("%.4f",ad[1]) + "  Slope 2:"+String.format("%.4f",ad[2]) + "  Slope 3:"+String.format("%.4f",ad[3]) + "  Slope 4:"+String.format("%.4f",ad[4]);
        	functionLine  = new PolynomialFunction2D(ad);
        } else if (dimIndex == 2) { // Power ax^b
        	ad = Regression.getPowerRegression(xyseriescollection, 0);
        	title = title + "  Const:"+String.format("%.4f",ad[0])+"  Power:"+String.format("%.4f",ad[1]) ;
        	functionLine  = new PowerFunction2D(ad[0],ad[1]);
        }
       
        double minDomain = (double) DatasetUtilities.findMinimumDomainValue(xyseriescollection);
        double maxDomain = (double) DatasetUtilities.findMaximumDomainValue(xyseriescollection);
        XYDataset xydataset = DatasetUtilities.sampleFunction2D(functionLine, minDomain, maxDomain, 100, "Fitted Regression Line");
        xyplot.setDataset(1, xydataset);
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer1.setSeriesPaint(0, Color.blue);
        xyplot.setRenderer(1, xylineandshaperenderer1);
        JFreeChart jfreechart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

        final ChartPanel chartPanel = new ChartPanel(jfreechart, false);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 600));
        this.setLayout(new BorderLayout());
        this.add(chartPanel,BorderLayout.CENTER);
	}
	
	private class PopupListener extends MouseAdapter {
		PopupListener() {
		}

		public void mousePressed(final MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(final MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				JPopupMenu popup = new JPopupMenu();

				JMenuItem menuItem = new JMenuItem("Save as Image");
				menuItem.setActionCommand("saveimage");
				menuItem.addActionListener(RegressionPlotPanel.this);
				popup.add(menuItem);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		// Save Image
		ImageUtil imgutil = new ImageUtil(this, "png");
		imgutil.removeWaring();
		return;
	}

}
