package com.fixedit.fixitadmin.Activities.Orders;

import com.fixedit.fixitadmin.Services.SubServiceModel;

public interface AddToCartCallbacks {
    public void addedToCart(SubServiceModel services, int quantity,int position);
    public void deletedFromCart(SubServiceModel services,int position);
    public void quantityUpdate(SubServiceModel services, int quantity,int position);

}
