package com.fixedit.fixitadmin.Servicemen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fixedit.fixitadmin.R;
import com.fixedit.fixitadmin.Services.AddSubService;
import com.fixedit.fixitadmin.Services.SubServiceModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServicemanListAdapter extends RecyclerView.Adapter<ServicemanListAdapter.ViewHolder> {
    Context context;
    ArrayList<ServicemanModel> itemlist;
    ServicemenListAdapterCallbacks callbacks;

    public ServicemanListAdapter(Context context, ArrayList<ServicemanModel> itemlist, ServicemenListAdapterCallbacks callbacks) {
        this.context = context;
        this.itemlist = itemlist;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_service_item_layout, viewGroup, false);
        ServicemanListAdapter.ViewHolder viewHolder = new ServicemanListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        final ServicemanModel model = itemlist.get(i);
        holder.name.setText(model.getName());
        holder.role.setText(model.getRole());
        if (model.getImageUrl() != null || !model.getImageUrl().equalsIgnoreCase("")) {
            Glide.with(context).load(model.getImageUrl()).into(holder.image);
        } else {
            Glide.with(context).load(R.drawable.ic_profile_plc).into(holder.image);

        }


        if (model.isActive()) {
            holder.activate.setChecked(true);
        } else {
            holder.activate.setChecked(false);
        }

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callbacks.onServiceDeleted(model);
                PopupMenu popup = new PopupMenu(context, holder.options);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                //handle menu1 click
                                Intent i = new Intent(context, AddServicemen.class);
                                i.putExtra("id", model.getId());

                                context.startActivity(i);
                                return true;
                            case R.id.action_delete:
                                //handle menu2 click
                                callbacks.onServicemanDeleted(model);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AddServicemen.class);
                i.putExtra("id", model.getId());
                context.startActivity(i);
            }
        });

        holder.activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    callbacks.onServicemanStatusChanged(model, isChecked);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView options;
        TextView name, role;
        Switch activate;
        CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            options = itemView.findViewById(R.id.options);
            name = itemView.findViewById(R.id.name);
            activate = itemView.findViewById(R.id.activate);
            role = itemView.findViewById(R.id.role);
            image = itemView.findViewById(R.id.image);

        }
    }

    public interface ServicemenListAdapterCallbacks {
        public void onServicemanStatusChanged(ServicemanModel model, boolean value);

        public void onServicemanDeleted(ServicemanModel model);
    }
}
