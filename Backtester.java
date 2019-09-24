/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backtester;
import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;

class Time
{
    public int hour;
    public int min;
    public int count;
    
    public Time(int hour, int min)
    {
        this.hour = hour;
        this.min = min;
        count = 0;
    }
}

class TradingSession
{
    public int day;
    public Time start_time;
    public Time end_time;
    
    public TradingSession(int day, Time start_time, Time end_time)
    {
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    
    public String GetString()
    {
        return day + "  " + start_time.hour + ":" + start_time.min + " " + end_time.hour + ":" + end_time.min;
    }
}

class PriceRecord
{
    public Calendar date;
    public double open;
    public double high;
    public double low;
    public double close;
    public int volume;
    
    public PriceRecord(Calendar date, double open, double high, double low, double close, int volume)
    {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}

class Chart
{
    public int length;
    public int period;
    public long[] date;
    public double[] open;
    public double[] high;
    public double[] low;
    public double[] close;
    public double[] volume;
    public Calendar next_date;
    public int record_number;
    public ArrayList indicators;
    public ArrayList indicator_properties;
        
    public Chart(int length, int period)
    {
        indicators = new ArrayList();
        indicator_properties = new ArrayList();
        record_number = 0;
        next_date = null;
        this.length = length;
        this.period = period;
        this.date = new long[length];
        this.open = new double[length];
        this.high = new double[length];
        this.low = new double[length];
        this.close = new double[length];
        this.volume = new double[length];
    }
        
    
}

class SimpleMovingAverage
{
    public int period = 5;
    public double[] values;
    
    public SimpleMovingAverage(Chart chart)
    {
        values = new double[chart.length];
    }
    
    public void Calculate(Chart chart)
    {        
        double sum = 0;        
        
        if (chart.record_number >= period - 1)
        {
        
            for (int i = 0; i < period; i++)
            {
                sum += chart.close[chart.length - 1 - (period - 1) + i];
            }        
            sum /= period;

        }
        
        values[chart.length - 1] = sum;
        
        
    }
}

class ExponentialMovingAverage
{
    public int period = 5;
    public double[] values;
    public int beg_id;
    
    public ExponentialMovingAverage(Chart chart)
    {
        values = new double[chart.length];
        beg_id = 0;
    }
    
    public void Calculate(Chart chart)
    {        
        double ema = 0;       
                
        if (chart.record_number == period)
        {
            ema = chart.close[chart.length - 1];
            beg_id = chart.record_number;
        }
        else if (chart.record_number > period)
        {
            double prev_ema = values[chart.length - 2];  
            double k = 2d / (double)(period + 1);
            ema = (chart.close[chart.length - 1] * k) + (prev_ema * (1 - k));
        }
                
        
        values[chart.length - 1] = ema;
        
        
    }
}

class EMAMacd
{    
    public int period1 = 4;
    public int period2 = 8;
    public ExponentialMovingAverage ema1;
    public ExponentialMovingAverage ema2;
    public double[] values;
    public int beg_id;
    
    public EMAMacd(Chart chart)
    {
        beg_id = 0;
        
        ema1 = new ExponentialMovingAverage(chart);
        ema1.period = period1;
        
        ema2 = new ExponentialMovingAverage(chart);
        ema2.period = period2;
        
        values = new double[chart.length];
    }
    
    public void Calculate(Chart chart)
    {        
        
        ema1.Calculate(chart);
        ema2.Calculate(chart);  
        
        if (ema1.beg_id > 0 && ema2.beg_id > 0)
        {
        
            if (chart.record_number >= ema1.beg_id &&
                    chart.record_number >= ema2.beg_id)
            {

                values[chart.length - 1] = ema1.values[chart.length - 1] - ema2.values[chart.length - 1];                

                if (beg_id == 0)
                {
                    beg_id = chart.record_number;

                }

            }
        
        }
        
        
    }
}

class TradingSystem
{
    public Chart chart;
    public ArrayList trading_sessions;
    
    public TradingSystem()
    {
        int chart_length = 1000000;
        int chart_period = 20;
        chart = new Chart(chart_length, chart_period);
        
        ExponentialMovingAverage ema = new ExponentialMovingAverage(chart);
        chart.indicators.add(ema);
        
        EMAMacd macd = new EMAMacd(chart);
        chart.indicators.add(macd);
        
        
        // note:  for some reason java cant identify object classes by name so we need to do it literally..
        
        // name, panel number?
        chart.indicator_properties.add(new Object[]{"exponential moving average"});
        chart.indicator_properties.add(new Object[]{"ema macd"});
        
        
        trading_sessions = new ArrayList();
        trading_sessions.add(new TradingSession(Calendar.SUNDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.MONDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.MONDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.TUESDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.TUESDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.WEDNESDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.WEDNESDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.THURSDAY, new Time(0, 0), new Time(17, 0)));
        trading_sessions.add(new TradingSession(Calendar.THURSDAY, new Time(17, 15), new Time(23, 59)));
        trading_sessions.add(new TradingSession(Calendar.FRIDAY, new Time(0, 0), new Time(17, 0)));
        
        
        int count = 0;
        for (int i = 0; i < 24; i++)
        {
            for (int m = 0; m < 60; m++)
            {
//                System.out.println(i + "  " + m);
                
                for (int si = 0; si < trading_sessions.size(); si++)
                {
                    TradingSession session = (TradingSession)trading_sessions.get(si);
                    
//                    System.out.println("    " + session.start_time.hour + " " + session.start_time.min);
                    
                    if (i == session.start_time.hour &&
                            m == session.start_time.min)
                    {
                        session.start_time.count = count;
                    }
                }
                
                
                for (int si = 0; si < trading_sessions.size(); si++)
                {
                    TradingSession session = (TradingSession)trading_sessions.get(si);
                    if (i == session.end_time.hour &&
                            m == session.end_time.min)
                    {
                        session.end_time.count = count;
                    }
                }
                
                count++;
            }
        }
        
    }
    
    public void update(PriceRecord price_record)
    {
        
        boolean session_open = false;
                        
        boolean complete = false;
        int count = 0;
        for (int h = 0; h < 24; h++)
        {
            for (int m = 0; m < 60; m++)
            {
                if (h == price_record.date.get(Calendar.HOUR_OF_DAY) &&
                        m == price_record.date.get(Calendar.MINUTE))
                {
                    complete = true;
                    break;
                }

                count++;
            }

            if (complete) break;
        }
        
        for (int si = 0; si < trading_sessions.size(); si++)
        {
            TradingSession session = (TradingSession)trading_sessions.get(si);

            if (price_record.date.get(Calendar.DAY_OF_WEEK) == session.day &&
                    count >= session.start_time.count &&
                    count < session.end_time.count)
            {
                session_open = true;
                break;
            }
        }
        
        if (session_open == true)
        {
            if (chart.next_date == null)
            {
                chart.next_date = Calendar.getInstance();
                chart.next_date.clear();
                chart.next_date.set(Calendar.YEAR, price_record.date.get(Calendar.YEAR));
                chart.next_date.set(Calendar.MONTH, price_record.date.get(Calendar.MONTH));
                chart.next_date.set(Calendar.DAY_OF_MONTH, price_record.date.get(Calendar.DAY_OF_MONTH));
                Calendar prev_date = (Calendar)chart.next_date.clone();
                while (true)
                { 
                    prev_date = (Calendar)chart.next_date.clone();
                    chart.next_date.add(Calendar.MINUTE, chart.period);
                    if (chart.next_date.compareTo(price_record.date) > 0)
                    {
                        break;
                    }
                }   
                chart.date[chart.length - 1] = prev_date.getTimeInMillis();
                chart.open[chart.length - 1] = price_record.open;
                chart.high[chart.length - 1] = price_record.high;
                chart.low[chart.length - 1] = price_record.low;
                chart.close[chart.length - 1] = price_record.close;
                chart.volume[chart.length - 1] = price_record.volume;
                
//                for (int i = 0; i < chart.length; i++)
//                {
//                    System.out.print(chart.close[i] + " ");
//                }
//                System.out.println();
            }
            else
            {
                if (price_record.date.compareTo(chart.next_date) >= 0)
                {
                    
                    
                    for (int i = 0; i < chart.indicator_properties.size(); i++)
                    {
                        Object[] ind = (Object[])chart.indicator_properties.get(i);
                        if (ind[0].toString().equals("simple moving average"))
                        {
                            SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i); 
                            ob.Calculate(chart);
                        }
                        else if (ind[0].toString().equals("exponential moving average"))
                        {
                            ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i); 
                            ob.Calculate(chart);                            
                        }
                        else if (ind[0].toString().equals("ema macd"))
                        {
                            EMAMacd ob = (EMAMacd)chart.indicators.get(i); 
                            ob.Calculate(chart);
                        }
                    }
                    
//                    for (int i = 0; i < chart.indicators.size(); i++)
//                    {
//                        if (chart.indicators.get(i).getClass().getSimpleName().equals("SimpleMovingAverage"))
//                        {
//                            SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i);
//                            ob.Calculate(chart);
//                        }
//                        else if (chart.indicators.get(i).getClass().getSimpleName().equals("ExponentialMovingAverage"))
//                        {
//                            ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i);
//                            ob.Calculate(chart);
//                        }
//                    }
                    
//                    for (int i = 0; i < chart.length; i++)
//                    {
//                        System.out.print(chart.close[i] + " ");
//                    }
//                    System.out.println();
//                    
//                    for (int i = 0; i < chart.length; i++)
//                    {
//                        for (int i2 = 0; i2 < chart.indicators.size(); i2++)
//                        {
//                            SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i2);
//
//                            System.out.print(ob.values[i] + " ");
//                        }
//                    }
//                        
//                    System.out.println();
//                    System.out.println();
                    
                    
                    Calendar prev_date = (Calendar)chart.next_date.clone();
                    while (true)
                    {
                        prev_date = (Calendar)chart.next_date.clone();
                        chart.next_date.add(Calendar.MINUTE, chart.period);
                        if (chart.next_date.compareTo(price_record.date) > 0)
                        {
                            break;
                        }
                    }   
                     
                    
                    for (int i = 1; i < chart.length; i++)
                    {
                        chart.date[i - 1] = chart.date[i];
                        chart.open[i - 1] = chart.open[i];
                        chart.high[i - 1] = chart.high[i];
                        chart.low[i - 1] = chart.low[i];
                        chart.close[i - 1] = chart.close[i];
                        chart.volume[i - 1] = chart.volume[i];
                        
                        for (int i2 = 0; i2 < chart.indicator_properties.size(); i2++)
                        {
                            Object[] ind = (Object[])chart.indicator_properties.get(i2);
                            if (ind[0].toString().equals("simple moving average"))
                            {
                                SimpleMovingAverage ob = (SimpleMovingAverage)chart.indicators.get(i2);                            
                                ob.values[i - 1] = ob.values[i];
                            }
                            else if (ind[0].toString().equals("exponential moving average"))
                            {
                                ExponentialMovingAverage ob = (ExponentialMovingAverage)chart.indicators.get(i2);                            
                                ob.values[i - 1] = ob.values[i];
                            }
                            else if (ind[0].toString().equals("ema macd"))
                            {
                                EMAMacd ob = (EMAMacd)chart.indicators.get(i2);  
                                ob.ema1.values[i - 1] = ob.ema1.values[i];
                                ob.ema2.values[i - 1] = ob.ema2.values[i];
                                ob.values[i - 1] = ob.values[i];
                            }
                        }
                        

                        
                    }
                    
                    
                    chart.date[chart.length - 1] = prev_date.getTimeInMillis();
                    chart.open[chart.length - 1] = price_record.open;
                    chart.high[chart.length - 1] = price_record.high;
                    chart.low[chart.length - 1] = price_record.low;
                    chart.close[chart.length - 1] = price_record.close;
                    chart.volume[chart.length - 1] = price_record.volume;
                    
                    
                    chart.record_number++;
                    
                }
                else
                {
                    if (price_record.high > chart.high[chart.length - 1]) chart.high[chart.length - 1] = price_record.high;
                    if (price_record.low < chart.low[chart.length - 1]) chart.low[chart.length - 1] = price_record.low;
                    chart.close[chart.length - 1] = price_record.close;
                    
                }
            }
            
        }
        
    }
    
}

class Backtester
{
    public TradingSystem trading_system;
    
    public void perform()
    {
        try
        {
        
            String path = "C:\\Users\\Russell Brown\\Documents\\downloads3\\USDCAD_Candlestick_1_M_BID_01.01.2017-14.09.2019.csv";

            RandomAccessFile f = new RandomAccessFile(path, "rw");
            
            String line = "";
            int line_count = 0;
            boolean header = true;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
            Calendar date = Calendar.getInstance();
            date.clear();
                       
            trading_system = new TradingSystem();
                        
            while (true)
            {
            
                byte[] data = new byte[100000];            
                int size = f.read(data);
                
                //System.out.println(f.getChannel().position() + "  " + size + "  " + f.getChannel().size());
                
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        char c = (char)data[i];
                        if (c == '\n')
                        {
                            if (header == false)
                            {
                                
                                String[] array = line.split(",");
                                date.setTime(format.parse(array[0]));
                                
                                
                                PriceRecord price_record = new PriceRecord(
                                        date, 
                                        Double.valueOf(array[1]),
                                        Double.valueOf(array[2]),
                                        Double.valueOf(array[3]),
                                        Double.valueOf(array[4]),
                                        (int)(Double.valueOf(array[5]) * 1000000)
                                );
                                
                                trading_system.update(price_record);
                                
                                
                            }
                            line = "";
                            header = false;
                            line_count++;
                            
                            if (line_count > 50000)
                            {
                                size = -1;
                                break;
                            }
                        }
                        else if (c != '\r')
                        {
                            line += c;
                        }
                    }                    
                
                }
                
                
            
                if (size == -1) break;
                
            
            }
            
            
            
            f.close();
        
        
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
}

///**
// *
// * @author Russell Brown
// */
//public class Backtester {
//
//    /**
//     * @param args the command line arguments
//     */
////    public static void main(String[] args) {
////        // TODO code application logic here
////        
////        
////        
////    }
//    
//}
