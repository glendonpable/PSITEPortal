package app.psiteportal.com.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.ScrollingElectionFragment;
import app.psiteportal.com.psiteportal.TestActivity;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder> {

    private final List<Nominee> mNominees;
    private final Context context;
    private final LayoutInflater inflater;
    private String imageUrl;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int counter = 0;
    private int num_positions_needed;

    public ElectionAdapter(Context context, List<Nominee> nominees){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mNominees = new ArrayList<>(nominees);
    }

    @Override
    public int getItemCount() {
        return mNominees.size();
    }

    @Override
    public void onBindViewHolder(ElectionViewHolder holder, final int position) {
        Nominee n = mNominees.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
       // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
        holder.thumbNail.setImageUrl(n.getImageUrl(), imageLoader);
        holder.name.setText(n.getName());
        holder.institution.setText(n.getInstitution());

        num_positions_needed = AppController.getInstance().getNum_positions_needed();
        holder.checkbox.setChecked(mNominees.get(position).isSelected());
        holder.checkbox.setTag(mNominees.get(position));

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox ch = (CheckBox) view;
                Nominee nom = (Nominee) ch.getTag();
                //nom.setSelected(ch.isChecked());

                if(ch.isChecked()){
                    counter++;
                }else if(!ch.isChecked()){
                    counter--;
                }
                if(counter>num_positions_needed){
                    ch.setChecked(false);
                    counter--;
                    Toast.makeText(context, "You are only allowed to vote "+num_positions_needed+" nominee/s", Toast.LENGTH_SHORT).show();
                }else{
                    mNominees.get(position).setSelected(ch.isChecked());
                }

            }
        });

    }

    @Override
    public ElectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflater.
                inflate(R.layout.gp_nominees, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        return new ElectionViewHolder(itemView, context, mNominees);
    }

    public static class ElectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView pic;
        public TextView name;
        public TextView institution;
        public CheckBox checkbox;
        public Nominee nominee;
        public CircleImageView thumbNail;

        List<Nominee> nominees = new ArrayList<>();
        Context context;
        public ElectionViewHolder(View v, Context context, List<Nominee> nominees) {
            super(v);
            this.nominees = nominees;
            this.context = context;
            v.setOnClickListener(this);
            //pic = (ImageView) v.findViewById(R.id.pic);
            name = (TextView) v.findViewById(R.id.name);
            institution = (TextView) v.findViewById(R.id.institution);
            checkbox = (CheckBox)v.findViewById(R.id.electionCheckBox);
            thumbNail = (CircleImageView) v.findViewById(R.id.pic);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Nominee n = this.nominees.get(position);
            Bitmap bitmap = n.getBitmap();
            Toast.makeText(context,n.getName(), Toast.LENGTH_LONG).show();

            Log.d("pota", n.getName());
            Log.d("pota", n.getInstitution());


            Intent intent = new Intent(this.context, TestActivity.class);

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bytes = stream.toByteArray();
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("prof_pic", n.getImageUrl());
            intent.putExtra("name", n.getName());
            intent.putExtra("institution", n.getInstitution());
            intent.putExtra("contact", n.getContact());
            intent.putExtra("email", n.getEmail());
            intent.putExtra("address", n.getAddress());
            context.startActivity(intent);
            AppController.getInstance().setActivity("election");


//            Bundle bundle = new Bundle();
//            bundle.putString("prof_pic", n.getImageUrl());
//            bundle.putString("name", n.getName());
//            bundle.putString("institution", n.getInstitution());
//            bundle.putString("contact", n.getContact());
//            bundle.putString("email", n.getEmail());
//            bundle.putString("address", n.getAddress());
//            ScrollingElectionFragment fragment = new ScrollingElectionFragment();
//            FragmentManager fragmentManager = fragment.getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container, fragment);
//            fragmentTransaction.commit();

        }
    }

    public int getCounter(){
        return counter;
    }

    public List<Nominee> getNomineeList() {
        return mNominees;
    }

    public void animateTo(List<Nominee> nominees) {
        applyAndAnimateRemovals(nominees);
        applyAndAnimateAdditions(nominees);
        applyAndAnimateMovedItems(nominees);
    }

    private void applyAndAnimateRemovals(List<Nominee> newNominees) {
        for (int i = mNominees.size() - 1; i >= 0; i--) {
            final Nominee nominee = mNominees.get(i);
            if (!newNominees.contains(nominee)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Nominee> newNominees) {
        for (int i = 0, count = newNominees.size(); i < count; i++) {
            final Nominee nominee = newNominees.get(i);
            if (!mNominees.contains(nominee)) {
                addItem(i, nominee);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Nominee> newNominees) {
        for (int toPosition = newNominees.size() - 1; toPosition >= 0; toPosition--) {
            final Nominee nominee = newNominees.get(toPosition);
            final int fromPosition = mNominees.indexOf(nominee);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Nominee removeItem(int position) {
        final Nominee nominee = mNominees.remove(position);
        notifyItemRemoved(position);
        return nominee;
    }

    public void addItem(int position, Nominee nominee) {
        mNominees.add(position, nominee);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Nominee nominee = mNominees.remove(fromPosition);
        mNominees.add(toPosition, nominee);
        notifyItemMoved(fromPosition, toPosition);
    }


}
