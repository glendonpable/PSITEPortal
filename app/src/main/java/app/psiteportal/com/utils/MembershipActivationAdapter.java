package app.psiteportal.com.utils;

/**
 * Created by fmpdroid on 3/11/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.fragments.AnnouncementFragment;
import app.psiteportal.com.fragments.MembershipActivationFragment;
import app.psiteportal.com.fragments.MembershipFragment;
import app.psiteportal.com.model.Member;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;

import app.psiteportal.com.psiteportal.TestActivity;

public class MembershipActivationAdapter extends RecyclerView.Adapter<MembershipActivationAdapter.MembershipActivationViewHolder> {

    private final List<Member> mMembers;
    private final Context context;
//    private final LayoutInflater inflater;
    private static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static String username;

    public MembershipActivationAdapter(Context context, List<Member> members){
//        inflater = LayoutInflater.from(context);
        this.mMembers = new ArrayList<>(members);
        this.context = context;
//        this.mMembers = members;
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }


    @Override
    public void onBindViewHolder(final MembershipActivationViewHolder holder, final int position) {
        final Member m = mMembers.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
        holder.bind(m);

        /*
        holder.name.setText(m.getName());

        holder.email.setText(m.getEmail());
        if(m.getStatus().equals("Active")){
            holder.status.setText(Html.fromHtml("<font color='#00CD00'>" + m.getStatus() + "</font>"));
        }else{
            holder.status.setText(Html.fromHtml("<font color='#ff0000'>" + m.getStatus() + "</font>"));
        }

        if(!holder.thumbnail.equals(null)){
            Log.wtf("thumbnail", "not null");
            Picasso.with(context).load(m.getImageUrl()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //holder.thumbnail.setImageBitmap(bitmap);
                    int height = (bitmap.getHeight() * 512 / bitmap.getWidth());
                    Bitmap scale = Bitmap.createScaledBitmap(bitmap, 512,height, true);
                    holder.thumbnail.setImageBitmap(scale);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                holder.thumbnail.setImageDrawable(placeHolderDrawable);
                }
            });
        }else {
//            holder.thumbnail.setImageUrl(m.getImageUrl(), imageLoader);
            Log.wtf("thumbnail", "null");
            holder.thumbnail.setImageResource(R.drawable.ic_person_black_24dp);
        }*/
//        Glide.with(context).load(m.getImageUrl()).into(holder.thumbnail);
//        holder.thumbnail.setImageUrl(m.getImageUrl(), imageLoader);


    }

    @Override
    public MembershipActivationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_activation_content, viewGroup, false);
        return new MembershipActivationViewHolder(itemView, context, mMembers);
    }

    public static class MembershipActivationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {

        public final TextView name;
        public final TextView institution;
        public final TextView status;

        public final CircleImageView thumbnail;

        List<Member> members = new ArrayList<>();
        Context context;

        public String member_status;
        String eMail;
        public String getEmail(){
            return eMail;
        }
        public void setEmail(String eMail){
            this.eMail = eMail;
        }

        public MembershipActivationViewHolder(View v, Context context, List<Member> members) {
            super(v);
            this.context = context;
            this.members = members;
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
            v.setOnLongClickListener(this);

            name = (TextView) v.findViewById(R.id.member_name);
            institution = (TextView) v.findViewById(R.id.member_institution);
            status = (TextView) v.findViewById(R.id.member_status);

            thumbnail = (CircleImageView) v.findViewById(R.id.member_pic);

//            memberSwitch = (Switch) v.findViewById(R.id.member_switch);
        }
        private void bind(Member member){
            name.setText(member.getName());
            institution.setText(member.getInstitution());
//            if(member.getStatus().equals("Member")){
//                status.setText(Html.fromHtml("Status: <font color='#00CD00'>" + member.getStatus() + "</font>"));
//            }else{
//                status.setText(Html.fromHtml("Status: <font color='#ff0000'>" + member.getStatus() + "</font>"));
//            }
            if(member.getStatus().equals("Non-Member")){
                status.setText(Html.fromHtml("Position: <font color='#FF0000'>" + member.getStatus() + "</font>"));
            }else if(member.getStatus().equals("Member")){
                status.setText(Html.fromHtml("Position: <font color='#00CD00'>" + member.getStatus() + "</font>"));
            }else if(member.getStatus().equals("Representative-Institution")){
                status.setText(Html.fromHtml("Position: <font color='#00CD00'>" + member.getStatus() + "</font>"));
            }else if(member.getStatus().equals("Officer")||member.getStatus().equals("Admin")){
                status.setText(Html.fromHtml("Position: <font color='#0000FF'>" + member.getStatus() + "</font>"));
            }else if(member.getStatus().equals("President")){
                status.setText(Html.fromHtml("Position: <font color='#FF00FF'>" + member.getStatus() + "</font>"));
            }

            if(!thumbnail.equals(null)){
//                Log.wtf("thumbnail", "not null");
                Glide.with(context).load(member.getImageUrl()).into(thumbnail);
//                Picasso.with(context).load(member.getImageUrl()).into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        //holder.thumbnail.setImageBitmap(bitmap);
//                        int height = (bitmap.getHeight() * 512 / bitmap.getWidth());
//                        Bitmap scale = Bitmap.createScaledBitmap(bitmap, 512,height, true);
//                        thumbnail.setImageBitmap(scale);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//    //                holder.thumbnail.setImageDrawable(placeHolderDrawable);
//                    }
//                });
            }else {
    //            holder.thumbnail.setImageUrl(m.getImageUrl(), imageLoader);
                Log.wtf("thumbnail", "null");
                thumbnail.setImageResource(R.drawable.ic_person_black_24dp);
            }
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Member m= this.members.get(position);

        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            Member m= this.members.get(position);
            username = m.getUsername();
            member_status = m.getStatus();
            AppController.getInstance().setEmail(m.getEmail());
            AppController.getInstance().setName(m.getName());
            AppController.getInstance().setInstitution(m.getInstitution());
            AppController.getInstance().setProf_pic(m.getImageUrl());
            AppController.getInstance().setAddress(m.getAddress());
            AppController.getInstance().setContact(m.getContact());
            return false;
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderIcon(R.drawable.ic_warning);

            if(MembershipActivationFragment.user_usertype.contains("Officer")
                    || MembershipActivationFragment.user_usertype.equals("President")) {
                menu.setHeaderTitle("Select Action");
                menu.add(0, v.getId(), 0, "Disable User");
                if(member_status.equals("Non-Member")||MembershipActivationFragment.user_usertype.equals("Officer-Member")){

                }else {
                    menu.add(0, v.getId(), 0, "Change Position");
                }
            }
            menu.add(0, v.getId(), 0, "View Profile");
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
