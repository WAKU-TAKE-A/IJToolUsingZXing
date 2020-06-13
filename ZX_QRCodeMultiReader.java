import ij.*;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import java.awt.Frame;
import java.util.EnumMap;
import java.util.Map;

/*
 * The MIT License
 *
 * Copyright 2020 Takehito Nishida.
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

/**
 * com.google.zxing.qrcode.QRCodeWriter.
 */
public class ZX_QRCodeMultiReader implements ij.plugin.filter.ExtendedPlugInFilter {
    // constant var.
    private static final int FLAGS = DOES_8G | DOES_RGB;
     
    // static var.
    private static boolean isTryHarder = false;
    private static boolean enRefData = false;
    
    // var.
    private String cmd = "";
    private ImagePlus src = null;
    private ResultsTable resTab = null;
    private RoiManager roiMan = null;

    @Override
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        cmd = command.trim();
        
        GenericDialog gd = new GenericDialog(cmd + " ...");
        gd.addCheckbox("try_harder", isTryHarder);
        gd.addCheckbox("enable_refresh_data", enRefData);
        gd.showDialog();

        if (gd.wasCanceled()) {
            return DONE;
        }
        else {
            isTryHarder = (boolean)gd.getNextBoolean();
            enRefData = (boolean)gd.getNextBoolean();
            return FLAGS;
        }
    }
    
    @Override
    public void setNPasses(int nPasses) {
        // do nothing
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        src = imp;
        return FLAGS;
    }

    @Override
    public void run(ImageProcessor ip) {        
        try {
            BufferedImage bi_src = src.getBufferedImage();
            LuminanceSource ls_src = new BufferedImageLuminanceSource(bi_src);
            HybridBinarizer hb_src = new HybridBinarizer(ls_src);
            BinaryBitmap bb_src = new BinaryBitmap(hb_src);
            
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, isTryHarder);
            
            Result[] results = new QRCodeMultiReader().decodeMultiple(bb_src, hints);
            
            if (results != null && 0 < results.length) {
                resTab = getResultsTable(false);
                roiMan = getRoiManager(false, true);

                if (enRefData) {
                    resTab.reset();
                    roiMan.reset();
                }

                for (Result result : results) {
                    showData(result);
                }
            }       
        }
        catch(NotFoundException ex) {
            IJ.error(ex.toString());
        }
    }
    
    private void showData(Result res)
    {  
        float cx = (res.getResultPoints()[0].getX() + res.getResultPoints()[2].getX()) / 2;
        float cy = (res.getResultPoints()[0].getY() + res.getResultPoints()[2].getY()) / 2;
        float ang = (float)(180 / Math.PI *Math.atan((res.getResultPoints()[1].getY() - res.getResultPoints()[2].getY()) / (res.getResultPoints()[2].getX() - res.getResultPoints()[1].getX())));

        // set the ResultsTable
        resTab.incrementCounter();
        resTab.addValue("CenterX", cx);
        resTab.addValue("CenterY", cy);
        resTab.addValue("Angle", ang);
        resTab.addValue("String", res.getText());
        resTab.show("Results");
        
        // set the ROI      
        float[] xPoints = new float[4];
        float[] yPoints = new float[4];
        
        xPoints[0] = res.getResultPoints()[0].getX();
        yPoints[0] = res.getResultPoints()[0].getY();
        xPoints[1] = res.getResultPoints()[1].getX();
        yPoints[1] = res.getResultPoints()[1].getY();
        xPoints[2] = res.getResultPoints()[2].getX();
        yPoints[2] = res.getResultPoints()[2].getY();
        xPoints[3] = xPoints[2] + (xPoints[0] - xPoints[1]);
        yPoints[3] = yPoints[2] + (yPoints[0] - yPoints[1]);
        PolygonRoi proi = new PolygonRoi(xPoints, yPoints, Roi.POLYGON);
        
        roiMan.addRoi(proi);
        int num_roiMan = roiMan.getCount();
        roiMan.select(num_roiMan - 1);
        roiMan.rename(num_roiMan - 1, String.valueOf(num_roiMan) + "_" + res.getText());
    }
    
    /**
     * get the ResultsTable or create a new ResultsTable
     * @param enReset reset or not
     * @return ResultsTable
     */
    private ResultsTable getResultsTable(boolean enReset)
    {
        ResultsTable rt = ResultsTable.getResultsTable();

        if(rt == null || rt.getCounter() == 0)
        {
            rt = new ResultsTable();
        }

        if(enReset)
        {
            rt.reset();
        }

        rt.show("Results");

        return rt;
    }

    /**
     * get the RoiManager or create a new RoiManager
     * @param enReset reset or not
     * @param enShowNone show none or not
     * @return RoiManager
     */
    private RoiManager getRoiManager(boolean enReset, boolean enShowNone)
    {
        Frame frame = WindowManager.getFrame("ROI Manager");
        RoiManager rm = null;

        if (frame == null)
        {
            rm = new RoiManager();
            rm.setVisible(true);
        }
        else
        {
            rm = (RoiManager)frame;
        }

        if(enReset)
        {
            rm.reset();
        }

        if(enShowNone)
        {
            rm.runCommand("Show None");
        }

        return rm;
    }
}
