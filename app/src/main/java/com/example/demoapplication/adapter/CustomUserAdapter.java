package com.example.demoapplication.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.databinding.CustomUserDataRowBinding;
import com.example.demoapplication.databinding.FragmentUserListBinding;
import com.example.demoapplication.fragments.UserDetailsFragment;
import com.example.demoapplication.fragments.UserListFragment;
import com.example.demoapplication.network.ApiCalls;
import com.example.demoapplication.network.NetworkObserver;
import com.example.demoapplication.network.ResponseCallback;
import com.example.demoapplication.services.SharedPreferencesOperations;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomUserAdapter  extends RecyclerView.Adapter<CustomUserAdapter.CustomUserViewHolder> {
    Activity context;
    List<User> users;
    List<User> ids = new ArrayList<>();
    String search;
    ActivityMainBinding activityMainBinding;
    CustomUserDataRowBinding binding;
    FragmentUserListBinding fragmentUserListBinding;
    SharedPreferencesOperations sh;
    UserDao userDao;
    boolean setCheck;
    boolean delete;

    public CustomUserAdapter(List<User> users, Activity context, boolean setCheck, String search, boolean delete, List<User> ids){
        this.ids = ids;
        this.search = search;
        this.delete = delete;
        this.setCheck = setCheck;
        this.users = users;
        this.context = context;
        this.activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        this.binding = CustomUserDataRowBinding.inflate(LayoutInflater.from(context));
        this.fragmentUserListBinding = FragmentUserListBinding.inflate(LayoutInflater.from(context));
        try {
            this.sh = new SharedPreferencesOperations(context);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

    }
    public CustomUserAdapter(List<User> users, Activity context, boolean setCheck, String search, boolean delete){
        this.search = search;
        this.delete = delete;
        this.setCheck = setCheck;
        this.users = users;
        this.context = context;
        this.activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(context));
        this.binding = CustomUserDataRowBinding.inflate(LayoutInflater.from(context));
        this.fragmentUserListBinding = FragmentUserListBinding.inflate(LayoutInflater.from(context));
        try {
            this.sh = new SharedPreferencesOperations(context);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public CustomUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomUserDataRowBinding binding = CustomUserDataRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomUserViewHolder(binding, ids);
    }

    public void deleteUser(@NonNull CustomUserViewHolder holder){
        int uid = users.get(holder.getAdapterPosition()).uid;
        if (NetworkObserver.isConnected(context)){
            new ApiCalls(context,fragmentUserListBinding.getRoot()).deleteUserApi(uid, new ResponseCallback() {
                @Override
                public void onSuccess() {
                    users.remove(holder.getAdapterPosition());
                    UserListFragment.setNoDataFound();
                    notifyItemRemoved(holder.getAdapterPosition());
                }

                @Override
                public void onError() {

                }
            });

        } else {
            userDao.deleteName(uid);
            users.remove(holder.getAdapterPosition());
            UserListFragment.setNoDataFound();
            notifyItemRemoved(holder.getAdapterPosition());
            Snackbar.make(binding.getRoot(), "No Internet Available", Snackbar.LENGTH_SHORT).show();
        }
    }
    public void deleteUser(User user, CustomUserViewHolder holder){
        int uid = user.uid;
        if (NetworkObserver.isConnected(context)){
            new ApiCalls(context,fragmentUserListBinding.getRoot()).deleteUserApi(uid, new ResponseCallback() {
                @Override
                public void onSuccess() {
                    users.remove(user);
                    UserListFragment.setNoDataFound();
                    notifyItemRemoved(holder.getAdapterPosition());
                }

                @Override
                public void onError() {

                }
            });

        } else {
            userDao.deleteName(uid);
            users.remove(holder.getAdapterPosition());
            UserListFragment.setNoDataFound();
            notifyItemRemoved(holder.getAdapterPosition());
            Snackbar.make(binding.getRoot(), "No Internet Available", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomUserViewHolder holder, int position) {
        userDao = DBInstance.getUserDao(context);

        if (users.get(position).name != null){
            String name = users.get(holder.getAdapterPosition()).name;
            String email = users.get(holder.getAdapterPosition()).email;
            if (!Objects.equals(search, "")){
                String replacement = "<font color='#FF0000'>"+search+"</font>";
                if (name.toLowerCase().contains(search.toLowerCase())){
                    name = name.toLowerCase().replaceAll(search, replacement);
                }
                if (email.toLowerCase().contains(search.toLowerCase())){
                    email = email.toLowerCase().replaceAll(search, replacement);
                }
            }
            holder.name.setText(HtmlCompat.fromHtml(name, HtmlCompat.FROM_HTML_MODE_LEGACY));
            holder.email.setText(HtmlCompat.fromHtml(email, HtmlCompat.FROM_HTML_MODE_LEGACY));
            if (users.get(holder.getAdapterPosition()).profilePic != null){
                holder.image.setImageBitmap(BitmapFactory.decodeByteArray(users.get(holder.getAdapterPosition()).profilePic, 0, users.get(holder.getAdapterPosition()).profilePic.length));
            }
            if (delete){
                ids.forEach(user -> deleteUser(user, holder));
            }
            holder.edit.setOnClickListener(v -> {
                UserDetailsFragment userDetailsFragment = new UserDetailsFragment(users.get(holder.getAdapterPosition()).uid, true);
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(activityMainBinding.fragment1.getId(), userDetailsFragment).setReorderingAllowed(true).addToBackStack(null);
                fragmentTransaction.commit();
            });
            holder.delete.setOnClickListener(v -> {
                new AlertDialog.Builder(context).setTitle("Delete").setMessage("Are You Sure?").setPositiveButton("Yes", (dialog, which) -> {
                    deleteUser(holder);
                }).setNegativeButton("Cancel", (dialog, which) -> {

                }).show();
            });


            holder.cardView.setOnClickListener(v -> {
                if (holder.checkBox.isChecked()){
                    holder.linearLayout.setBackgroundColor(Color.WHITE);
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setVisibility(View.GONE);
                    ids.remove(users.get(position));
                    UserListFragment.setSelectCheck(false);
                    UserListFragment.setIdsList(ids);
                } else {
                    UserDetailsFragment userDetailsFragment = new UserDetailsFragment(users.get(holder.getAdapterPosition()).uid, false);
                    FragmentActivity fragmentActivity = (FragmentActivity) context;
                    FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(activityMainBinding.fragment1.getId(), userDetailsFragment).setReorderingAllowed(true).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            holder.cardView.setOnLongClickListener(v -> {
                if (!holder.checkBox.isChecked()){
                    holder.linearLayout.setBackgroundColor(Color.parseColor("#536671"));
                    holder.checkBox.setChecked(true);
                    holder.checkBox.setVisibility(View.VISIBLE);
                    ids.add(users.get(position));
                    UserListFragment.setIdsList(ids);
                } else {
                    holder.linearLayout.setBackgroundColor(Color.WHITE);
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setVisibility(View.GONE);
                    ids.remove(users.get(position));
                    UserListFragment.setSelectCheck(false);
                    UserListFragment.setIdsList(ids);
                }
                return true;
            });
            if (setCheck){
                holder.linearLayout.setBackgroundColor(Color.parseColor("#536671"));
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(View.VISIBLE);
                ids.add(users.get(position));
                System.out.println(ids);
                UserListFragment.setIdsList(ids);
            }
            holder.checkBox.setOnClickListener( v -> {
                if (!holder.checkBox.isChecked()){
                    holder.linearLayout.setBackgroundColor(Color.WHITE);
                    holder.checkBox.setVisibility(View.GONE);
                    ids.remove(users.get(position));
                    UserListFragment.setSelectCheck(false);
                    UserListFragment.setIdsList(ids);
                } else {
                    holder.linearLayout.setBackgroundColor(Color.parseColor("#536671"));
                    ids.add(users.get(position));
                    UserListFragment.setIdsList(ids);
                }
            });
        } else {
            holder.cardView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return users == null? 0 : users.size();
    }

    static class CustomUserViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView name;
        AppCompatTextView email;
        AppCompatImageView image;
        AppCompatImageButton edit;
        AppCompatImageButton delete;
        CardView cardView;
        AppCompatCheckBox checkBox;
        LinearLayoutCompat linearLayout;


        CustomUserViewHolder(CustomUserDataRowBinding binding, List<User> ids){
            super(binding.getRoot());
            name = binding.name;
            email = binding.email;
            image = binding.userImage;
            edit = binding.buttonEdit;
            delete = binding.buttonDelete;
            cardView = binding.cardView;
            checkBox = binding.checkBox;
            linearLayout = binding.listView;
            if (!ids.isEmpty()){
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
        }
    }
}
