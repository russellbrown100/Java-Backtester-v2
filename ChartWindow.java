/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backtester;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JPanel;


class Formatters
{
    public static java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd  HH:mm:ss");
    public static java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat("yyyyMMdd  HH:mm:ss:SSS");
    public static DecimalFormat pf5 = new DecimalFormat("0.00000");
    
    public static DecimalFormat CalculatePrecision(int precision)
    {
        String string = "0";
        
        if (precision > 0)
        {
        
            for (int i = 0; i < precision; i++)
            {
                if (i == 0) string += ".";

                string += "0";
            }
        
        }
        
        return new DecimalFormat(string);
    }
    
}


class ChartPanel
{
    public int left;
    public int top;
    public int width;
    public int height;
    public boolean show_title;
    public boolean show_data;
    public boolean show_upper_indicators;
    public boolean show_lower_indicators;
    public boolean show_x_axis;
    public int y_axis_interval;
    public int y_axis_precision;
}

class ChartWindow extends JPanel {
    
    public BufferedImage image;
    public Chart chart;
    public int bars_per_window = 100;
    public int position;
    public int left;
    public int top;
    public int width;
    public int height;
    public int bar_shift;
    public double price_margin = 0.25;
    public double bar_margin = 0.20;
    public int mouse_x;
    public int mouse_y;
    public ChartPanel panel;
    public ChartPanel panel2;
    
    public ChartWindow()
    {
        mouse_x = 0;
        mouse_y = 0;
        
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                mouse_x = evt.getX();
                mouse_y = evt.getY();
                repaint();
            }
        });
    }
    
    public void init()
    {
        position = 0;
        bars_per_window = 200;
        bar_shift = 2;
        
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black)); 
        
        setBounds(1, 1, width, height);          
        setBackground(Color.white);
        
        int step = 320;
        
        panel = new ChartPanel();
        panel.left = left;
        panel.top = top;
        panel.width = width - step;
        panel.height = height - 200;
        panel.y_axis_interval = 20;
        panel.y_axis_precision = 5;
        panel.show_title = true;
        panel.show_x_axis = false;
        panel.show_data = true;
        panel.show_upper_indicators = true;
        panel.show_lower_indicators = false;
        
        
        panel2 = new ChartPanel();
        panel2.left = left;
        panel2.top = panel.top + panel.height;
        panel2.width = width - step;
        panel2.height = 120;
        panel2.y_axis_interval = 20;
        panel2.y_axis_precision = 5;
        panel2.show_title = false;
        panel2.show_x_axis = true;
        panel2.show_data = false;
        panel2.show_upper_indicators = false;
        panel2.show_lower_indicators = true;
        
    }
    
    public void PaintPanel(Graphics2D graphics, ChartPanel panel)
    {
        
            
        double bar_width = (double)panel.width / bars_per_window;
        

        graphics.setClip(0, 0, this.getWidth(), this.getHeight());
        graphics.setColor(Color.black);                       
        graphics.draw(new Rectangle.Double(panel.left, panel.top, panel.width, panel.height));
        
        
        if (chart == null) return;
        
        
        
        // calculate price range
        
        double min_price = Double.MAX_VALUE;
        double max_price = Double.MIN_VALUE;
        
        if (panel.show_data == true)
        {

            for (int i = 0; i < bars_per_window - bar_shift; i++)
             {
                int id = chart.length - 1 - chart.record_number + position + i;
                 
                if (chart.high[id] > max_price) max_price = chart.high[id];
                if (chart.low[id] < min_price) min_price = chart.low[id]; 
                                    
                 
             }
       
        }
        else
        {
            for (int i = 0; i < bars_per_window - bar_shift; i++)
            {
                int id = chart.length - 1 - chart.record_number + position + i;
                
                for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
                {
                    Object[] ind = (Object[])chart.indicator_properties.get(i2);
                    
                    if (panel.show_upper_indicators == true)
                    {
                        if (ind[0].toString().equals("simple moving average"))
                        {
                            SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i2); 
                            if (ob.values[id] > max_price) max_price = ob.values[id];
                            if (ob.values[id] < min_price) min_price = ob.values[id];
                        }
                        else if (ind[0].toString().equals("exponential moving average"))
                        {
                            ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);   
                            if (id >= ob.beg_id)
                            {
                                if (ob.values[id] > max_price) max_price = ob.values[id];
                                if (ob.values[id] < min_price) min_price = ob.values[id];
                            }
                        }
                    }
                    
                    if (panel.show_lower_indicators == true)
                    {
                    
                        if (ind[0].toString().equals("ema macd"))
                        {
                            EMAMacd ob = (EMAMacd)chart.indicators.get(i2); 
                            if (id >= ob.beg_id)
                            {
                                if (ob.values[id] > max_price) max_price = ob.values[id];
                                if (ob.values[id] < min_price) min_price = ob.values[id];
                            }

                        }
                    
                    }
                    
                }
            
            }
            
        }
        
       
        
        double price_range = max_price - min_price;
        
        
        price_range = max_price - min_price;

        if (price_range == 0) price_range = 1;

        max_price = max_price + (price_range * price_margin);
        min_price = min_price - (price_range * price_margin);

        price_range = max_price - min_price;
        
        
        graphics.setClip(panel.left, panel.top, panel.width, panel.height);
        
        // candles
        
        Color up_color = Color.green;
        Color dn_color = Color.red;

        double x = panel.left;
        
        if (panel.show_data == true)
        {
        
            for (int i = 0; i < bars_per_window - bar_shift; i++)
            {
                int id = chart.length - 1 - chart.record_number + position + i;


                if (    chart.open[id] > 0 &&
                            chart.high[id] > 0 &&
                            chart.low[id] > 0 &&
                            chart.close[id] > 0)
                {

                    double op = (panel.top + panel.height) - (panel.height * ((chart.open[id] - min_price) / price_range));
                    double hp = (panel.top + panel.height) - (panel.height * ((chart.high[id] - min_price) / price_range));
                    double lp = (panel.top + panel.height) - (panel.height * ((chart.low[id] - min_price) / price_range));
                    double cp = (panel.top + panel.height) - (panel.height * ((chart.close[id] - min_price) / price_range));

                    if (op < cp)
                    {       
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), hp, x + (bar_width / 2), op));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), lp, x + (bar_width / 2), cp));

                        graphics.setColor(dn_color);
                        graphics.fill( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), op, bar_width - ((bar_width / 2) * (bar_margin * 2)), cp - op));
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), op, bar_width - ((bar_width / 2) * (bar_margin * 2)), cp - op));

                    }
                    else if (op > cp)
                    {       
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), hp, x + (bar_width / 2), cp));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), lp, x + (bar_width / 2), op));

                        graphics.setColor(up_color);
                        graphics.fill( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), cp, bar_width - ((bar_width / 2) * (bar_margin * 2)), op - cp));
                        graphics.setColor(Color.BLACK);
                        graphics.draw( new Rectangle2D.Double(x + ((bar_width / 2) * bar_margin), cp, bar_width - ((bar_width / 2) * (bar_margin * 2)), op - cp));

                    }
                    else if (op == cp)
                    {                                
                        graphics.setColor(Color.black);
                        graphics.draw( new Line2D.Double(x + ((bar_width / 2) * bar_margin), op, x + (bar_width / 2), op));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), cp, x + (bar_width / 2) + ((bar_width / 2) * (1 - bar_margin)), cp));
                        graphics.draw( new Line2D.Double(x + (bar_width / 2), hp, x + (bar_width / 2), lp));
                    }

                }

                graphics.setColor(Color.black);

                x += bar_width;


            }

        }
        
        
        // indicators
        
        
        graphics.setColor(Color.blue);
        
        x = panel.left;
                
        for (int i = 1; i < bars_per_window - bar_shift; i++)
        {
            int id = chart.length - 1 - chart.record_number + position + i;
            
            int candle_id = id - (chart.length - 1 - chart.record_number) - 1;
            
                
            for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
            {                
                Object[] ind = (Object[])chart.indicator_properties.get(i2);
                    
                if (panel.show_upper_indicators)
                {
                        
                    if (ind[0].toString().equals("exponential moving average"))
                    {
                        ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);
                        double p = ob.values[id];
                        double p2 = ob.values[id - 1];

                        if (candle_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }
                
                }
                
                if (panel.show_lower_indicators)
                {
                    if (ind[0].toString().equals("ema macd"))
                    {
                        EMAMacd ob = (EMAMacd)chart.indicators.get(i2);
                        double p = ob.values[id];
                        double p2 = ob.values[id - 1];

                        if (candle_id >= ob.beg_id)
                        {                    
                            double sp = (panel.top + panel.height) - (panel.height * ((p - min_price) / price_range));
                            double sp2 = (panel.top + panel.height) - (panel.height * ((p2 - min_price) / price_range));
                            double bar_center = x + (bar_width / 2);     
                            {
                                graphics.draw( new Line2D.Double(bar_center, sp, bar_center - bar_width, sp2));
                            }
                        }

                    }
                }
                
                

            }
                                
                
            
            

            x += bar_width;

            
        }
        
        
        graphics.setClip(0, 0, this.getWidth(), this.getHeight());
        
        // y axis
        
        
        graphics.setColor(Color.black);
                                
        double intervals = panel.y_axis_interval;

        double gap = (double)(panel.height - 10) / intervals;
        double dy = (double)(panel.top + 5);
        double dx = panel.left + panel.width;
        
        
        for (int i = 0; i <= intervals; i++)
        {                        


            graphics.draw(new Line2D.Double(dx, dy, dx + 10, dy));


            int ix = panel.left + panel.width;
            int iy = panel.top;
            while (iy <= panel.top + panel.height+1)
            {
                if (iy > dy && iy - 1 <= dy)
                {
                    double price_percent = (double)(iy - panel.top) / (double)panel.height;
                    
                    
                    double price = max_price - (price_percent * price_range);
                    
                    String price_string = "";
                                        
                    price_string = Formatters.CalculatePrecision(panel.y_axis_precision).format(price);
                    
                    graphics.drawString(price_string, ix + 15, iy + 4);

                    break;

                }
                iy++;
            }




            dy += gap;
        }
        
        
        // x axis

        if (panel.show_x_axis == true)
        {
            


        
            dy = panel.top + panel.height;
            dx = panel.left + panel.width - (bar_width * bar_shift);


            Calendar date = Calendar.getInstance();
            date.clear();
            Font font = graphics.getFont();
            FontMetrics metrics = graphics.getFontMetrics(font);

            int bar_count = 0;
            boolean first_bar = true;
            
            double max_w = 0;
            
            for (int i = bars_per_window - bar_shift - 1; i >= 0; i--)
            {
                int id = chart.length - 1 - chart.record_number + position + i;
                     
                date.setTimeInMillis(chart.date[id]);
                
                String string = Formatters.df.format(date.getTime());
                double w = metrics.getStringBounds(string, graphics).getWidth();
                
                if (w > max_w) max_w = w;
            }
            
            max_w += 10;
            
            int bars_per_interval = 1;
            double bw = bar_width;
            while (true)
            {
                bw += bar_width;
                bars_per_interval++;
                if (bw >= max_w) break;
            }
            
            
                
            for (int i = bars_per_window - bar_shift - 1; i >= 0; i--)
            {
                int id = chart.length - 1 - chart.record_number + position + i;

                date.setTimeInMillis(chart.date[id]);

                String string = Formatters.df.format(date.getTime());
                double w = metrics.getStringBounds(string, graphics).getWidth();
                double h = metrics.getStringBounds(string, graphics).getHeight();

                if (first_bar == true)
                {
                    graphics.draw(new Line2D.Double(dx + (bar_width / 2d), dy, dx + (bar_width / 2d), dy + 10));
                    graphics.drawString(string,(int) (dx - (w / 2)), (int)(dy + 10 + h));          
                    bar_count = 0;
                    first_bar = false;
                }
                else
                {

                    if (bar_count >= bars_per_interval-1)
                    {
                        if ((int) (dx - (w / 2)) > 0)
                        {
                            graphics.draw(new Line2D.Double(dx + (bar_width / 2d), dy, dx + (bar_width / 2d), dy + 10));
                            graphics.drawString(string,(int) (dx - (w / 2)), (int)(dy + 10 + h));                          
                        }
                        bar_count = 0;
                    }
                    else
                    {
                        bar_count++;
                    }

                }


                dx -= bar_width;

            }
        
        }
        
        
    }
    
    
    
    public void PaintCrossHair(Graphics2D graphics, ChartPanel panel)
    {

            
        double bar_width = (double)panel.width / bars_per_window;
        
        
        
        // calculate price range
        
        double min_price = 0;
        double max_price = 0;
        
      
        for (int i = 0; i < bars_per_window - bar_shift; i++)
        {
            int id = chart.length - 1 - chart.record_number + position + i;
                        
            if (i == 0)
            {
                max_price = chart.high[id];
                min_price = chart.low[id];
            }
            else
            {
                if (chart.high[id] > max_price) max_price = chart.high[id];
                if (chart.low[id] < min_price) min_price = chart.low[id]; 
            }
        }
        
        double price_range = max_price - min_price;
        
        
        price_range = max_price - min_price;

        if (price_range == 0) price_range = 1;

        max_price = max_price + (price_range * price_margin);
        min_price = min_price - (price_range * price_margin);

        price_range = max_price - min_price;
        
              
        
        
        // paint cross hair
        

        if (mouse_x > panel.left && mouse_x < panel.left + panel.width &&
            mouse_y > panel.top && mouse_y < panel.top + panel.height)
        {
            
            

            
            graphics.setColor(Color.black);
            
            
            Font font = graphics.getFont();
            FontMetrics metrics = graphics.getFontMetrics(font);

            double price_percent = (double)(mouse_y - panel.top) / (double)panel.height;
            double price = max_price - (price_percent * price_range);
            graphics.drawLine(panel.left, mouse_y - 1, panel.left + panel.width + 10, mouse_y - 1); 
            graphics.drawLine(mouse_x, top, mouse_x, top + this.panel.height + 10);
            
            // price marker
            
            graphics.setColor(Color.black);              
            String price_string = Formatters.CalculatePrecision(panel.y_axis_precision).format(price);   
            
            
            
                        
            double pw = metrics.getStringBounds(price_string, graphics).getWidth();
            
            graphics.fill(new Rectangle.Double(panel.left + panel.width + 10, mouse_y - 6, pw + 5, 12));
            
            
            graphics.drawLine(mouse_x, panel.top, mouse_x, this.panel.top + panel.height + 10);
            
            
            graphics.setColor(Color.white);
            
            graphics.drawString(price_string, panel.left + panel.width + 15, mouse_y + 4);

            // date marker
            
            double dx = panel.left + panel.width - (bar_width * bar_shift);
             double dy = this.top + this.panel.height;
            Calendar date = Calendar.getInstance();
            date.clear();
   
           
            
            for (int i = bars_per_window - bar_shift - 1; i >= 0; i--)
            {
                int id = chart.length - 1 - chart.record_number + position + i;

                date.setTimeInMillis(chart.date[id]);

                String string = Formatters.df.format(date.getTime());
                double w = metrics.getStringBounds(string, graphics).getWidth();
                double h = metrics.getStringBounds(string, graphics).getHeight();

                if (dx <= mouse_x)
                {
                    
                    int sx = (int)(dx - (w / 2));
                    int sy = (int)(dy + 10 + h);
                    
                    graphics.setColor(Color.black);
                    graphics.fill(new Rectangle.Double(dx - 2 - (w / 2), dy + 15 - 2, w + 2, h));
                    
                    graphics.setColor(Color.white);
                    graphics.drawString(string, sx, sy);
                    
                    
                    graphics.setColor(Color.black);
                    
                    int step = 70;
                    int step2 = 170;
                    
                    
                    graphics.drawString("Date:", panel.left + panel.width + step, top + 15);
                    graphics.drawString("Open:", panel.left + panel.width + step, top + (15 * 2));
                    graphics.drawString("High:", panel.left + panel.width + step, top + (15 * 3));
                    graphics.drawString("Low:", panel.left + panel.width + step, top + (15 * 4));
                    graphics.drawString("Close:", panel.left + panel.width + step, top + (15 * 5));
                    
                    double open = chart.open[id];
                    double high = chart.high[id];
                    double low = chart.low[id];
                    double close = chart.close[id];

                    graphics.drawString(Formatters.df.format(date.getTime()), panel.left + panel.width + step2, top + 15);   
                    graphics.drawString(Formatters.pf5.format(open), panel.left + panel.width + step2, top + (15 * 2));
                    graphics.drawString(Formatters.pf5.format(high), panel.left + panel.width + step2, top + (15 * 3));
                    graphics.drawString(Formatters.pf5.format(low), panel.left + panel.width + step2, top + (15 * 4));
                    graphics.drawString(Formatters.pf5.format(close), panel.left + panel.width + step2, top + (15 * 5));

                    
                    break;
                }
                
                dx -= bar_width;
            }
            
            

                     
            
            
            
            

        }



        
    }
    
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
      //  PaintPanel((Graphics2D)g, panel);
     //   PaintCrossHair((Graphics2D)g, panel);
        
        if (chart != null)
        {
            image = new BufferedImage(this.getWidth(), this.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();            
            g2.setBackground(Color.white);
            g2.fill(new Rectangle.Double(0, 0, image.getWidth(), image.getHeight()));      
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
            
            PaintPanel(g2, panel);
            PaintPanel(g2, panel2);
           // PaintCrossHair(g2, panel);
            
            g.drawImage(image, 0, 0, null);
        }
        else if (image != null)
        {
            g.drawImage(image, 0, 0, null);            
        }
        
    }
    
    
}
