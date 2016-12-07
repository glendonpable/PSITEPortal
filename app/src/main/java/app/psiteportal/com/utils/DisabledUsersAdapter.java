package app.psiteportal.com.utils;

/**
 * Created by fmpdroid on 3/11/2016.
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.fragments.DisabledUsersFragment;
import app.psiteportal.com.model.Member;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;

import app.psiteportal.com.psiteportal.TestActivity;

public class DisabledUsersAdapter extends RecyclerView.Adapter<DisabledUsersAdapter.DisabledUsersViewHolder> {

    private final List<Member> mMembers;
    private final Context context;
    private final LayoutInflater inflater;
    public static String username;


    public DisabledUsersAdapter(Context context, List<Member> members){
        inflater = LayoutInflater.from(context);
        this.mMembers = new ArrayList<>(members);
        this.context = context;
//        this.mMembers = members;
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }


    @Override
    public void onBindViewHolder(DisabledUsersViewHolder holder, final int position) {
        final Member m = mMembers.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
//        holder.bind(m);
        holder.name.setText(m.getName());

        holder.email.setText(m.getEmail());
//        if(m.getStatus().equals("Active")){
//            holder.status.setText(Html.fromHtml("<font color='#00CD00'>" + m.getStatus() + "</font>"));
//        }else{
//            holder.status.setText(Html.fromHtml("<font color='#ff0000'>" + m.getStatus() + "</font>"));
//        }


    }

    @Override
    public DisabledUsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = inflater.inflate(R.layout.disabled_users_content, viewGroup, false);
        return new DisabledUsersViewHolder(itemView, context, mMembers);
    }

    public static class DisabledUsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {

        public final TextView name;
        public final TextView email;
//        public final TextView status;

        List<Member> members = new ArrayList<>();
        Context context;

        String eMail;
        public String getEmail(){
            return eMail;
        }
        public void setEmail(String eMail){
            this.eMail = eMail;
        }

        public DisabledUsersViewHolder(View v, Context context, List<Member> members) {
            super(v);
            this.context = context;
            this.members = members;
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
            v.setOnLongClickListener(this);

            name = (TextView) v.findViewById(R.id.member_name);
            email = (TextView) v.findViewById(R.id.member_email);
//            status = (TextView) v.findViewById(R.id.member_status);

//            memberSwitch = (Switch) v.findViewById(R.id.member_switch);
        }
//        private void bind(Member member){
//            name.setText(member.getName());
//            email.setText(member.getEmail());
//            if(member.getStatus().equals("Active")){
//                status.setText(Html.fromHtml("<font color='#00CD00'>" + member.getStatus() + "</font>"));
//            }else{
//                status.setText(Html.fromHtml("<font color='#ff0000'>" + member.getStatus() + "</font>"));
//            }
//        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Member m= this.members.get(position);
            Toast.makeText(view.getContext(), m.getName(), Toast.LENGTH_SHORT).show();
        }

//        @Override
//        public boolean onLongClick(final View v) {
//            int position = getAdapterPosition();
//            Member m= this.members.get(position);
//            AppController.getInstance().setEmail(m.getEmail());
//            new AlertDialog.Builder(v.getContext())
//                    .setTitle("Restore User")
//                    .setMessage("Are you sure to restore this user?")
//                    .setIcon(R.drawable.ic_warning)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            restoreUser(email, v);
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, null).show();
//            return false;
//        }
        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            Member m= this.members.get(position);
            username = m.getUsername();
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderIcon(R.drawable.ic_warning);
            //menu.setHeaderTitle("Select Action");
            menu.add(0, v.getId(), 0, "Restore User");
        }


    }


    public List<Member> getMemberList() {
        return mMembers;
    }

    public void animateTo(List<Member> members) {
        applyAndAnimateRemovals(members);
        applyAndAnimateAdditions(members);
        applyAndAnimateMovedItems(members);
    }

    private void applyAndAnimateRemovals(List<Member> newMembers) {
        for (int i = mMembers.size() - 1; i >= 0; i--) {
            final Member member = mMembers.get(i);
            if (!newMembers.contains(member)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Member> newMembers) {
        for (int i = 0, count = newMembers.size(); i < count; i++) {
            final Member member = newMembers.get(i);
            if (!mMembers.contains(member)) {
                addItem(i, member);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Member> newMembers) {
        for (int toPosition = newMembers.size() - 1; toPosition >= 0; toPosition--) {
            final Member member = newMembers.get(toPosition);
            final int fromPosition = mMembers.indexOf(member);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Member removeItem(int position) {
        final Member member = mMembers.remove(position);
        notifyItemRemoved(position);
        return member;
    }

    public void addItem(int position, Member member) {
        mMembers.add(position, member);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Member member = mMembers.remove(fromPosition);
        mMembers.add(toPosition, member);
        notifyItemMoved(fromPosition, toPosition);
    }




}
