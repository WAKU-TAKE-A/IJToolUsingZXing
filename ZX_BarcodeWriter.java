import ij.*;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.Code93Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.oned.UPCEWriter;

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
 * com.google.zxing.oned.***Writer.
 */
public class ZX_BarcodeWriter implements ij.plugin.filter.ExtendedPlugInFilter {
    // constant var.
    private static final int FLAGS = NO_IMAGE_REQUIRED;
    
    private static final BarcodeFormat[] FORMATS = { 
        BarcodeFormat.CODE_39, 
        BarcodeFormat.CODE_93, 
        BarcodeFormat.CODE_128, 
        BarcodeFormat.CODABAR, 
        BarcodeFormat.ITF,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E,
        BarcodeFormat.EAN_8,
        BarcodeFormat.EAN_13      
    };
    
    private static final String[] STR_FORMATS = {
        "CODE_39", "CODE_93", "CODE_128", "CODABAR", "ITF", 
        "UPC_A", "UPC_E", "EAN_8", "EAN_13"
    };
    
    // staic var.
    private static int indFormat = 0;
    private static String content = "123abc";
    private static int sizeW = 128;
    private static int sizeH = 128;
    private static int margin = 3;
    private static Boolean isGS1 = false;
    
    // var.
    String cmd = "";

    @Override
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        cmd = command.trim();
        
        GenericDialog gd = new GenericDialog(cmd + " ...");
        gd.addChoice("format", STR_FORMATS, STR_FORMATS[indFormat]);
        gd.addStringField("content", content,3);
        gd.addNumericField("width", sizeW, 0);
        gd.addNumericField("height", sizeH, 0);
        gd.addNumericField("margin", margin, 0);
        gd.addCheckbox("gs1", isGS1);
        gd.showDialog();

        if (gd.wasCanceled())
        {
            return DONE;
        }
        else
        {
            indFormat = gd.getNextChoiceIndex();
            content = gd.getNextString();
            sizeW = (int)gd.getNextNumber();
            sizeH = (int)gd.getNextNumber();
            margin = (int)gd.getNextNumber();
            isGS1 = gd.getNextBoolean();
            return IJ.setupDialog(imp, FLAGS);
        }
    }
    
    @Override
    public void setNPasses(int nPasses) {
        // do nothing
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return FLAGS;
    }

    @Override
    public void run(ImageProcessor ip) {        
        try {
            BufferedImage image = null;
            BitMatrix bitMatrix = null;
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, margin);
            hints.put(EncodeHintType.GS1_FORMAT, isGS1);

            if (STR_FORMATS[indFormat].equals("CODE_39")) {
                Code39Writer writer = new Code39Writer();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("CODE_93")) {
                Code93Writer writer = new Code93Writer();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("CODE_128")) {
                Code128Writer writer = new Code128Writer();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("CODABAR")) {
                CodaBarWriter writer = new CodaBarWriter();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("ITF")) {
                ITFWriter writer = new ITFWriter();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("UPC_A")) {
                UPCAWriter writer = new UPCAWriter();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("UPC_E")) {
                UPCEWriter writer = new UPCEWriter();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("EAN_8")) {
                EAN8Writer writer = new EAN8Writer();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else if (STR_FORMATS[indFormat].equals("EAN_13")) {
                EAN13Writer writer = new EAN13Writer();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
            else {
                Code39Writer writer = new Code39Writer();
                bitMatrix = writer.encode(content, FORMATS[indFormat], sizeW, sizeH, hints);
            }
                      
            image = MatrixToImageWriter.toBufferedImage(bitMatrix);          
            String titleRet = WindowManager.getUniqueName(FORMATS[indFormat].toString());
            ImagePlus ret = new ImagePlus(titleRet, image);
            ret.show();
        }
        catch(WriterException ex) {
            IJ.error(ex.toString());
        }
    }
}
