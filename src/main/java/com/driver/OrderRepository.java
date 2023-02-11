package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    Map<String,Order> orderMap = new HashMap<>();
    Map<String,DeliveryPartner> deliveryPartnerMap = new HashMap<>();
    Map<String,List<String>> orderPartnerPair = new HashMap<>();
    Map<String,String> partnerAssignOrderPair = new HashMap<>();


    public void addOrder(Order order) {
        orderMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerMap.put(partnerId,deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        List<String> list = orderPartnerPair.getOrDefault(partnerId, new ArrayList<>());
        list.add(orderId);
        orderPartnerPair.put(partnerId, list);
        partnerAssignOrderPair.put(orderId, partnerId);
        DeliveryPartner partner = deliveryPartnerMap.get(partnerId);
        partner.setNumberOfOrders(list.size());
    }

    public Order getOrderById(String orderId) {
        if(orderMap.containsKey(orderId)) return orderMap.get(orderId);
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(deliveryPartnerMap.containsKey(partnerId)) return deliveryPartnerMap.get(partnerId);
        return null;
    }

    public int getOrderCountByPartnerId(String partnerId) {
        if(deliveryPartnerMap.containsKey(partnerId)){
            return deliveryPartnerMap.get(partnerId).getNumberOfOrders();
        }
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        if(orderPartnerPair.containsKey(partnerId)){
            return orderPartnerPair.get(partnerId);
        }
        return null;
    }

    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        for(String orderid : orderMap.keySet()) orders.add(orderid);
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        int count =0;
        for(String order : orderMap.keySet()){
            if(!partnerAssignOrderPair.containsKey(order)) count++;
        }
        return count;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int countOfOrders = 0;
        List<String> list = orderPartnerPair.get(partnerId);
        int deliveryTime = Integer.parseInt(time.substring(0, 2)) * 60 + Integer.parseInt(time.substring(3));
        for (String s : list) {
            Order order = orderMap.get(s);
            if (order.getDeliveryTime() > deliveryTime) {
                countOfOrders++;
            }
        }
        return countOfOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        String time = "";
        List<String> list = orderPartnerPair.get(partnerId);
        int deliveryTime = 0;
        for (String s : list) {
            Order order = orderMap.get(s);
            deliveryTime = Math.max(deliveryTime, order.getDeliveryTime());
        }
        int hour = deliveryTime / 60;
        String sHour = "";
        if (hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }

        int min = deliveryTime % 60;
        String sMin = "";
        if (min < 10) {
            sMin = "0" + String.valueOf(min);
        } else {
            sMin = String.valueOf(min);
        }

        time = sHour + ":" + sMin;

        return time;
    }

    public void deleteOrderById(String orderId) {
        if(orderMap.containsKey(orderId)) orderMap.remove(orderId);
        if(partnerAssignOrderPair.containsKey(orderId)){
            String partnerId = partnerAssignOrderPair.get(orderId);
            partnerAssignOrderPair.remove(orderId);
            List<String> orderids = orderPartnerPair.get(partnerId);
            int index = orderids.indexOf(orderId);
            orderids.remove(index);
        }
    }

    public void deletePartnerById(String partnerId) {
        deliveryPartnerMap.remove(partnerId);
        for(String orderid : partnerAssignOrderPair.keySet()){
            if(partnerAssignOrderPair.get(orderid).equals(partnerId)) partnerAssignOrderPair.remove(orderid);
        }
        orderPartnerPair.remove(partnerId);
    }
}
