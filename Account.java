/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backtester;

import java.util.ArrayList;




class Account
{
    public ArrayList inventory_summary;
    public ArrayList inventory;
    
    
    public Account()
    {
        inventory_summary = new ArrayList();
        inventory = new ArrayList();
    }
        
    public void buy(String name, String currency, int quantity, double price, String transaction_type)
    {       
        // order quantity needs to be absolute values
        
        
        int current_qty = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            String smb = (String)arr[0];
            String type = (String)arr[3];
            if (smb.equals(name) && type.equals(transaction_type))
            {
                int qty = (int)arr[1];
                current_qty += qty;
            }
        }
        
        if (current_qty < 0)
        {
            double profit = 0;
            int counted_qty = 0;
            
            for (int i = 0; i < inventory.size(); )
            {
                Object[] arr = (Object[])inventory.get(i);                
                String smb = (String)arr[0];   
                String type = (String)arr[3];             
                
                boolean removed = false;
                
                if (smb.equals(name) && type.equals(transaction_type))
                {
                    
                    int qty = (int)arr[1];
                    double prc = (double)arr[2];
            
                    if (counted_qty + qty >= -quantity)
                    {
                        profit -= qty * (prc - price);

                        inventory.remove(i);
                        removed = true;

                        counted_qty += qty;
                        
                        int rem_qty = 0;
                        
                        for (int i2 = 0; i2 < inventory.size(); i2++)
                        {
                            Object[] arr2 = (Object[])inventory.get(i2);                
                            String smb2 = (String)arr2[0]; 
                            String type2 = (String)arr2[3];
                            if (smb2.equals(name) && type2.equals(transaction_type))
                            {
                                rem_qty += (int)arr2[1];
                            }
                        }

                        if (rem_qty == 0)
                        {
                            qty = counted_qty + quantity;
                            if (qty > 0)
                            {
                                inventory.add(new Object[]{smb, qty, price, type, currency});
                            }
                            break;
                        }

                    }
                    else
                    {
                        
                        profit -= (-quantity - counted_qty) * (prc - price);
                        qty -= (-quantity - counted_qty);
                        arr[1] = qty;
                        inventory.set(i, arr);
                        break;
                    }
                    
                    
                }
                
                if (removed == false)
                {
                    i++;
                }
            }
            
            if (transaction_type.equals("debit"))
            {
            
                for (int i = 0; i < inventory.size(); i++)
                {
                    Object[] arr = (Object[])inventory.get(i);                
                    String smb = (String)arr[0];        
                    if (smb.equals(currency))
                    {
                        int cash_quantity = (int)arr[1];

                        if (cash_quantity > 0)
                        {
                            cash_quantity -= quantity * price;
                            arr[1] = cash_quantity;     
                            inventory.set(i, arr);


                        }

                        break;
                    }
                }
            
            }
            else if (transaction_type.equals("credit"))
            {      
                for (int i = 0; i < inventory.size(); i++)
                {
                    Object[] arr = (Object[])inventory.get(i);                
                    String smb = (String)arr[0];        
                    if (smb.equals(currency))
                    {
                        int cash_quantity = (int)arr[1];

                        if (cash_quantity > 0)
                        {
                            cash_quantity += profit;
                            arr[1] = cash_quantity;     
                            inventory.set(i, arr);


                        }

                        break;
                    }
                }
                
            }
             
            
        }
        else
        {
            if (name.equals(currency))
            {
                inventory.add(new Object[]{name, quantity, price, transaction_type, currency});                
            }  
            else
            {
                if (transaction_type.equals("debit"))
                {                
                    for (int i = 0; i < inventory.size(); i++)
                    {
                        Object[] arr = (Object[])inventory.get(i);                
                        String smb = (String)arr[0];
                        String type = (String)arr[3];
                        if (smb.equals(currency) && type.equals(transaction_type))
                        {
                            int cash_quantity = (int)arr[1];

                            if (cash_quantity > 0)
                            {
                                cash_quantity -= quantity * price;
                                arr[1] = cash_quantity;     
                                inventory.set(i, arr);

                                inventory.add(new Object[]{name, quantity, price, transaction_type, currency});

                            }

                            break;
                        }
                    }
                }
                else if (transaction_type.equals("credit"))
                {
                    inventory.add(new Object[]{name, quantity, price, transaction_type, currency});                    
                }
                
            }
            
                        
        }
        
        
        
        
    }
    
    
    
    
    
    public void sell(String name, String currency, int quantity, double price, String transaction_type)
    {
             
        int current_qty = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            String smb = (String)arr[0];
            String type = (String)arr[3];
            if (smb.equals(name) && type.equals(transaction_type))
            {
                int qty = (int)arr[1];
                current_qty += qty;
            }
        }
        
        
        if (current_qty > 0)
        {
            double profit = 0;     
            int counted_qty = 0;

            for (int i = 0; i < inventory.size(); )
            {
                Object[] arr = (Object[])inventory.get(i);                
                String smb = (String)arr[0];     
                String type = (String)arr[3];  
                
                boolean removed = false;
                
                if (smb.equals(name) && type.equals(transaction_type))
                {
                    int qty = (int)arr[1];
                    double prc = (double)arr[2];
                    
                    
                    

                    if (counted_qty + qty <= quantity)
                    {
                        profit += qty * (price - prc);

                        inventory.remove(i);                    
                        removed = true;

                        counted_qty += qty;
                        
                        int rem_qty = 0;
                        
                        for (int i2 = 0; i2 < inventory.size(); i2++)
                        {
                            Object[] arr2 = (Object[])inventory.get(i2);                
                            String smb2 = (String)arr2[0];    
                            String type2 = (String)arr2[3];
                            if (smb2.equals(name) && type2.equals(transaction_type))
                            {
                                rem_qty += (int)arr2[1];
                            }
                        }
                        
                        if (rem_qty == 0)
                        {
                            qty = counted_qty - quantity;
                            if (qty < 0)
                            {
                                inventory.add(new Object[]{smb, qty, price, type, currency});
                            }                            
                            break;
                        }

                    }
                    else
                    {
                        profit += (quantity - counted_qty) * (price - prc);
                        qty -= (quantity - counted_qty);
                        arr[1] = qty;
                        inventory.set(i, arr);
                        break;
                    }
                    
                }
                
                if (removed == false)
                {
                    i++;
                }
            }
        
            if (transaction_type.equals("debit"))
            {
            
                for (int i = 0; i < inventory.size(); i++)
                {
                    Object[] arr = (Object[])inventory.get(i);                
                    String smb = (String)arr[0];     
                    String type = (String)arr[3];

                    if (smb.equals(currency))
                    {
                        int cash_quantity = (int)arr[1];

                        if (cash_quantity > 0)
                        {
                            cash_quantity += quantity * price;
                            arr[1] = cash_quantity;     
                            inventory.set(i, arr);                        
                        }

                        break;
                    }

                }
            
            }
            else if (transaction_type.equals("credit"))
            {
                for (int i = 0; i < inventory.size(); i++)
                {
                    Object[] arr = (Object[])inventory.get(i);                
                    String smb = (String)arr[0];     

                    if (smb.equals(currency))
                    {
                        int cash_quantity = (int)arr[1];

                        if (cash_quantity > 0)
                        {
                            cash_quantity += profit;
                            arr[1] = cash_quantity;     
                            inventory.set(i, arr);                        
                        }

                        break;
                    }

                }
            }
            
            
        }
        else
        {
            if (name.equals(currency))
            {
                inventory.add(new Object[]{name, -quantity, price, transaction_type, currency});                
            }
            else
            {              
                if (transaction_type.equals("debit"))
                {                
                    for (int i = 0; i < inventory.size(); i++)
                    {
                        Object[] arr = (Object[])inventory.get(i);                
                        String smb = (String)arr[0];

                        if (smb.equals(currency))
                        {
                            int cash_quantity = (int)arr[1];                    

                            if (cash_quantity > 0)
                            {
                                cash_quantity += quantity * price;
                                arr[1] = cash_quantity;     
                                inventory.set(i, arr);

                                inventory.add(new Object[]{name, -quantity, price, transaction_type, currency});

                            }

                            break;
                        }
                    }
                }
                else if (transaction_type.equals("credit"))
                {
                    inventory.add(new Object[]{name, -quantity, price, transaction_type, currency});              
                }
                
                
            }
            
            
            
             
        }
                
        
        
    }
    
    
    public void PrintInventory()
    {
        System.out.println("inventory:");
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            System.out.println(arr[0] + "  " + arr[1] + "  " + arr[2] + "  " + arr[3] + "  " + arr[4]);
        }
    }
    
    
    public int CalculateTotalQuantity(String name)
    {
        int result = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            String smb = (String)arr[0];
            if (smb.equals(name))
            {
                int qty = (int)arr[1];
                result += qty;
            }
        }
        
        return result;
    }
       
    public double CalculateAverageCost(String name)
    {
        int total_quantity = 0;
        double total_cost = 0;
        
        for (int i = 0; i < inventory.size(); i++)
        {
            Object[] arr = (Object[])inventory.get(i);
            String smb = (String)arr[0];
            if (smb.equals(name))
            {
                int qty = (int)arr[1];
                double prc = (double)arr[2];
                total_quantity += qty;
                total_cost += qty * prc;
            }
        }
        
        double result = 0;
        
        if (total_quantity != 0)
        {
            result = total_cost / total_quantity;
        }
        
        return result;
    }
    
    public double CalculateProfit(String name, double market_price)
    {
        double result = 0;
        
        double average_cost = CalculateAverageCost(name);
        int total_quantity = CalculateTotalQuantity(name);       
        
        if (total_quantity > 0)
        {
            result = (market_price - average_cost) * Math.abs(total_quantity);
        }
        else if (total_quantity < 0)
        {
            result = (average_cost - market_price) * Math.abs(total_quantity);
        }
        
        return result;
    }
    
    public void CalculateInventorySummary()
    {
        
        
        if (inventory_summary.size() > 1)
        {
            
            int i = 1;
        
            while (true)
            {
                Object[] arr = (Object[])inventory_summary.get(i);
                String symbol = (String)arr[0];
                
                {

                    boolean remove_symbol = true;

                    for (int i2 = 0; i2 < inventory.size(); i2++)
                    {
                        Object[] arr2 = (Object[])inventory.get(i2);
                        String symbol2 = (String)arr2[0];

                        if (symbol2.equals(symbol))
                        {
                            remove_symbol = false;
                            break;
                        }

                    }

                    if (remove_symbol)
                    {
                        inventory_summary.remove(i);
                        if (inventory_summary.size() == 1) break;
                    }
                    else
                    {
                        i++;
                        if (i >= inventory_summary.size()) break;
                    }
                
                }
                
                
                if (i >= inventory_summary.size()) break;

            }

        }
        
        
        for (int i3 = 0; i3 < inventory.size(); i3++)
        {
            Object[] arr3 = (Object[])inventory.get(i3);            
            String symbol3 = (String)arr3[0];
            
            boolean add_symbol = true;
            
            for (int i3a = 0; i3a < inventory_summary.size(); i3a++)
            {
                Object[] arr3a = (Object[])inventory_summary.get(i3a);
                String symbol3a = (String)arr3a[0];
                if (symbol3a.equals(symbol3))
                {
                    add_symbol = false;
                    int total_quantity = CalculateTotalQuantity(symbol3);
                    double average_cost = CalculateAverageCost(symbol3);                                        
                    arr3a[1] = total_quantity;
                    arr3a[2] = average_cost;
                    arr3a[3] = 0;
                    arr3a[4] = 0;
                    inventory_summary.set(i3a, arr3a);
                    break;
                }
            }
            
            if (add_symbol)
            {
                Object[] arr4 = new Object[]{symbol3, 0, 0, 0, 0};                                            
                
                int total_quantity = CalculateTotalQuantity(symbol3);
                double average_cost = CalculateAverageCost(symbol3);
                arr4[1] = total_quantity;
                arr4[2] = average_cost;
                arr4[3] = 0;
                arr4[4] = 0;                
                
                inventory_summary.add(arr4);                  
            }
            
            
            
        }
        
        
        
    }
    
    public double GetMarketPrice(String name)
    {
        double result = 0;
        
        for (int i4 = 0; i4 < inventory_summary.size(); i4++)
        {
            Object[] arr4 = (Object[])inventory_summary.get(i4);
            String symbol4 = (String)arr4[0];
            if (symbol4.equals(name))
            {
                result = (double)arr4[3];
                break;
            }
        }
        
        return result;
    }
    
    
    
    public double GetProfit(String name)
    {
        double result = 0;
        
        for (int i4 = 0; i4 < inventory_summary.size(); i4++)
        {
            Object[] arr4 = (Object[])inventory_summary.get(i4);
            String symbol4 = (String)arr4[0];
            if (symbol4.equals(name))
            {
                result = (double)arr4[4];
                break;
            }
        }
        
        return result;
    }
    
    public void UpdateInventorySummary(String name, double market_price)
    {
        
        
        for (int i4 = 0; i4 < inventory_summary.size(); i4++)
        {
            Object[] arr4 = (Object[])inventory_summary.get(i4);
            String symbol4 = (String)arr4[0];
            if (symbol4.equals(name))
            {
                int total_quantity = CalculateTotalQuantity(symbol4);
                double average_cost = CalculateAverageCost(symbol4);
                double profit = CalculateProfit(symbol4, market_price);
                arr4[1] = total_quantity;
                arr4[2] = average_cost;
                arr4[3] = market_price;
                arr4[4] = profit;
                inventory_summary.set(i4, arr4);
                break;
            }
        }
        
                
        
        
    }
    
    public double CalculateBalance()
    {
        double result = 0;
        
        // NOTE:  For Forex, we only need to be concerned about the debit balance,
        // for stocks, we need all position balances.
        
//        for (int i = 0; i < inventory_summary.size(); i++)
//        {
//            Object[] arr = (Object[])inventory_summary.get(i);
//            
//            result += (int)arr[1] * (double)arr[2];
//        }

        Object[] arr = (Object[])inventory_summary.get(0);
        result += (int)arr[1] * (double)arr[2];
        
        return result;
    }
    
    	
    
    public double CalculateEquity()
    {
        double result = 0;
        
        for (int i = 0; i < inventory_summary.size(); i++)
        {
            Object[] arr = (Object[])inventory_summary.get(i);
            result += Double.valueOf(arr[4].toString());
        }
        
        return result;
    }
	
	
	
	
	
    
    public void PrintInventorySummary()
    {
        System.out.println("inventory summary:");
        for (int i = 0; i < inventory_summary.size(); i++)
        {
            Object[] arr = (Object[])inventory_summary.get(i);
            
            System.out.println(String.format("%-8s", arr[0]) + 
                    String.format("%-10s", arr[1]) + 
                    String.format("%-10s", arr[2]) + 
                    String.format("%-10s", arr[3]) +
                    String.format("%-10s", arr[4]));
            
            
        }
    }
    
    public static void test1()
    {
             Account account = new Account();
        account.buy("CAD", "CAD", 100000, 1, "debit");
        
        account.CalculateInventorySummary();
        account.PrintInventorySummary();
        
        
        account.sell("USD", "CAD", 100000, 1.3569, "credit");
        
        account.CalculateInventorySummary();
        account.PrintInventorySummary();
        
        account.UpdateInventorySummary("USD", 1.3589);
        account.PrintInventorySummary();
        
        
        double balance = account.CalculateBalance();
        double equity = balance + account.CalculateEquity();        
        System.out.println(balance + "  " + equity);
        
        account.buy("USD", "CAD", Math.abs(account.CalculateTotalQuantity("USD")), account.GetMarketPrice("USD"), "credit");
        
        account.CalculateInventorySummary();
        account.PrintInventorySummary();
        
//        
//        balance = account.CalculateBalance();
//        equity = balance + account.CalculateEquity();
//        System.out.println(balance + "  " + equity);
//        

        
    }
    
}



