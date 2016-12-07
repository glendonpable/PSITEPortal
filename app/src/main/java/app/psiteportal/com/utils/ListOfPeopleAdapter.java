package app.psiteportal.com.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.TestActivity;

/**
 * Created by fmpdroid on 3/18/2016.
 */
public class ListOfPeopleAdapter extends RecyclerView.Adapter<ListOfPeopleAdapter.ListOfPeopleViewHolder> {

    private final List<Nominee> mNominees;
    private final Context context;
    private final LayoutInflater inflater;
    private String imageUrl;
    private static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private static int num_positions_needed;
    int lastCheckedPosition = 0;
    private static int counter = 0;
    RadioButton lastChecked = null;

    public ListOfPeopleAdapter(Context context, List<Nominee> nominees){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mNominees = new ArrayList<>(nominees);
    }

    @Override
    public int getItemCount() {
        return mNominees.size();
    }

    @Override
    public void onBindViewHolder(ListOfPeopleViewHolder holder, final int position) {
        final Nominee n = mNominees.get(position);
//        int position1 = getAdapterPosition();
//        Nominee n = this.nominees.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());


//        holder.checkbox.setChecked(nominees.get(position).isSelected());
//        holder.checkbox.setTag(nominees.get(position));
//
//        holder.checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CheckBox ch = (CheckBox) view;
//                Nominee nom = (Nominee) ch.getTag();
//
//                nom.setSelected(ch.isChecked());
//                nominees.get(position).setSelected(ch.isChecked());
//            }
//        });
//        holder.radioButton.setChecked(nominees.get(position).isSelected());
//        holder.radioButton.setTag(new Integer(position));
//        if(position == 0 && nominees.get(0).isSelected() && holder.radioButton.isChecked()){
//            lastCheckedPosition = 0;
//            lastChecked = holder.radioButton;
//        }
//        holder.radioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RadioButton rb = (RadioButton) view;
//                int clickedPos = ((Integer)rb.getTag()).intValue();
//
//                if(rb.isChecked())
//                {
//                    if(lastChecked != null)
//                    {
//                        lastChecked.setChecked(false);
//                        nominees.get(lastCheckedPosition).setSelected(false);
//                    }
//
//                    lastChecked = rb;
//                    lastCheckedPosition = clickedPos;
//                }
//                else
//                    lastChecked = null;
//
//                nominees.get(clickedPos).setSelected(rb.isChecked());
//            }
//        });

        num_positions_needed = AppController.getInstance().getNum_positions_needed();
        Log.wtf("adapter num needed", String.valueOf(num_positions_needed));

        /*
        holder.thumbNail.setImageUrl(n.getImageUrl(), imageLoader);
        holder.name.setText(n.getName());
        holder.institution.setText(n.getInstitution());
        holder.checkbox.setChecked(mNominees.get(position).isSelected());
        holder.checkbox.setTag(mNominees.get(position));

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                Nominee nom = (Nominee) cb.getTag();
                //nom.setSelected(ch.isChecked());
                try {
                    if (cb.isChecked()) {
                        counter++;
                    } else if (!cb.isChecked()) {
                        counter--;
                    }
                    if (counter > num_positions_needed) {
                        cb.setChecked(false);
                        counter--;
                        Toast.makeText(context, "You are only allowed to nominate " + num_positions_needed + " nominees", Toast.LENGTH_SHORT).show();
                    } else {
                        mNominees.get(position).setSelected(cb.isChecked());
                        Log.wtf("position is here", String.valueOf(mNominees.get(position).getName()));
                        Log.wtf("position is here", String.valueOf(nom.getName()));
                        Log.wtf("position is here", String.valueOf(n.getName()));
                    }
                }catch (Exception e){

                }

            }
        });
         */
        holder.bind(n);
    }

    @Override
    public ListOfPeopleViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflater.
                inflate(R.layout.list_of_people_content, viewGroup, false);

        //imageLoader = AppController.getInstance().getImageLoader();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return new ListOfPeopleViewHolder(itemView, context, mNominees);
    }

    public static class ListOfPeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView pic;
        public TextView name;
        public TextView institution;
        public TextView count;
        public Nominee nominee;
        public CircleImageView thumbNail;
        public CardView cardView;

        List<Nominee> nominees = new ArrayList<>();
        Context context;
        public ListOfPeopleViewHolder(View v, Context context, List<Nominee> nominees) {
            super(v);
            this.nominees = nominees;
            this.context = context;
            v.setOnClickListener(this);
            //pic = (ImageView) v.findViewById(R.id.pic);
            name = (TextView) v.findViewById(R.id.list_name);
            institution = (TextView) v.findViewById(R.id.list_institution);
            count = (TextView) v.findViewById(R.id.list_count);
            thumbNail = (CircleImageView) v.findViewById(R.id.list_pic);
            cardView = (CardView) v.findViewById(R.id.list_card_view);
        }
        private void bind(Nominee n){
            num_positions_needed = AppController.getInstance().getNum_positions_needed();
            int position = getAdapterPosition();
            n = this.nominees.get(position);
            final List nomi = new ArrayList();

            thumbNail.setImageUrl(n.getImageUrl(), imageLoader);
            name.setText(n.getName());
            institution.setText(n.getInstitution());
            if(n.getCount()!=null&&AppController.getInstance().getActivity().equals("election")) {
                count.setText("Number of times elected: " + n.getCount());
            }
            else if(n.getCount()!=null) {
                count.setText("Number of times nominated: " + n.getCount());
            }
            else {
                count.setVisibility(View.GONE);
            }
            if(n.isSelected()) {
                cardView.setBackground(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.cardview_border));
//                cardView.setBackground(context.getApplicationContext().getDrawable(R.drawable.cardview_border));
            }
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