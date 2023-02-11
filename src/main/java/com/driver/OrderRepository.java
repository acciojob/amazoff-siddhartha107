package com.driver;


import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {


    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> deliveryPartnerMap;
    private HashMap<String, String> orderPartnerMapping;
    private HashMap<String, HashSet<String>> partnerOrderMapping;

    public OrderRepository(){
        this.orderMap = new HashMap<>();
        this.deliveryPartnerMap = new HashMap<String, DeliveryPartner>();
        this.orderPartnerMapping = new HashMap<String, String>();
        this.partnerOrderMapping = new HashMap<String,HashSet<String >>();

    }

    public void addOrder(Order order){
        orderMap.put(order.getId(),order);

    }

    public void addPartner(String partnerId){
        deliveryPartnerMap.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if (orderMap.containsKey(orderId) && deliveryPartnerMap.containsKey(partnerId)) {
            HashSet<String> orders = new HashSet<String>();
            if (partnerOrderMapping.containsKey(partnerId)) {
                orders = partnerOrderMapping.get(partnerId);
            }
            orders.add(orderId);
            partnerOrderMapping.put(partnerId, orders);

            DeliveryPartner partner = deliveryPartnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
            orderPartnerMapping.put(orderId, partnerId);
        }
    }

    public Order getOrderById(String orderId){
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        Integer orderCount = 0;
        if(deliveryPartnerMap.containsKey(partnerId)){
            orderCount = deliveryPartnerMap.get(partnerId).getNumberOfOrders();
        }
        return orderCount;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        HashSet<String> orderlist = new HashSet<>();
        if (partnerOrderMapping.containsKey(partnerId)) {

            orderlist = partnerOrderMapping.get(partnerId);
        }
        return new ArrayList<>(orderlist);
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public int getCountOfUnassignedOrders() {
        int countOfOrders = 0;
        List<String> orders =  new ArrayList<>(orderMap.keySet());
        for(String orderId: orders){
            if(!orderPartnerMapping.containsKey(orderId)){
                countOfOrders += 1;
            }
        }
        return countOfOrders;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String timeS, String partnerId) {
        int hour = Integer.valueOf(timeS.substring(0, 2));
        int minutes = Integer.valueOf(timeS.substring(3));
        int time = hour*60 + minutes;

        int countOfOrders = 0;
        if(partnerOrderMapping.containsKey(partnerId)){
            HashSet<String> orders = partnerOrderMapping.get(partnerId);
            for(String order: orders){
                if(orderMap.containsKey(order)){
                    Order currOrder = orderMap.get(order);
                    if(time < currOrder.getDeliveryTime()){
                        countOfOrders += 1;
                    }
                }
            }
        }
        return countOfOrders;
    }


    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int time = 0;

        if(partnerOrderMapping.containsKey(partnerId)){
            HashSet<String> orders = partnerOrderMapping.get(partnerId);
            for(String order: orders){
                if(orderMap.containsKey(order)){
                    Order currOrder = orderMap.get(order);
                    time = Math.max(time, currOrder.getDeliveryTime());
                }
            }
        }

        int hour = time/60;
        int minutes = time%60;

        String hourInString = String.valueOf(hour);
        String minInString = String.valueOf(minutes);
        if(hourInString.length() == 1){
            hourInString = "0" + hourInString;
        }
        if(minInString.length() == 1){
            minInString = "0" + minInString;
        }

        return  hourInString + ":" + minInString;
    }

    public void deleteOrder(String orderId) {
        if(orderPartnerMapping.containsKey(orderId)){
            String partnerId = orderPartnerMapping.get(orderId);
            HashSet<String> orders = partnerOrderMapping.get(partnerId);
            orders.remove(orderId);
            partnerOrderMapping.put(partnerId, orders);

            //change order count of partner
            DeliveryPartner partner = deliveryPartnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
        }

        if(orderMap.containsKey(orderId)){
            orderMap.remove(orderId);
        }

    }

    public void deletePartner(String partnerId) {
        HashSet<String> orders = new HashSet<>();
        if(partnerOrderMapping.containsKey(partnerId)){
            orders = partnerOrderMapping.get(partnerId);
            for(String order: orders){
                if(orderPartnerMapping.containsKey(order)){

                    orderPartnerMapping.remove(order);
                }
            }
            partnerOrderMapping.remove(partnerId);
        }

        if(deliveryPartnerMap.containsKey(partnerId)){
            deliveryPartnerMap.remove(partnerId);
        }
    }

}
