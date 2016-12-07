package app.psiteportal.com.utils;

/**
 * Created by fmpdroid on 3/22/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Member;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;


/**
 * Created by fmpdroid on 3/11/2016.
 */

public class NomineesAdapter extends RecyclerView.Adapter<NomineesAdapter.NomineesAdapterViewHolder> {

    private final List<Nominee> mNominees;
    private final Context context;
    private final LayoutInflater inflater;

    public NomineesAdapter(Context context, List<Nominee> nominees){
        inflater = LayoutInflater.from(context);
        this.mNominees = new ArrayList<>(nominees);
        this.context = context;
//        this.mMembers = members;
    }

    @Override
    public int getItemCount() {
        return mNominees.size();
    }


    @Override
    public void onBindViewHolder(NomineesAdapterViewHolder holder, final int position) {
        final Nominee m = mNominees.get(position);
        //View v;
        //ImageView image = (ImageView) v.findViewById(R.id.pic);
        // new LoadImage(imv).execute(n.getImageUrl());

        //holder.pic.setImageBitmap(n.getBitmap());
//        holder.bind(m);
        holder.name.setText(m.getName());


    }

    @Override
    public NomineesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = inflater.inflate(R.layout.nominees, viewGroup, false);
        return new NomineesAdapterViewHolder(itemView, context, mNominees);
    }

    public static class NomineesAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView name;
//        public final TextView email;
//        public final TextView status;

        List<Nominee> nominees = new ArrayList<>();
        Context context;

        String eMail;
        public String getEmail(){
            return eMail;
        }
        public void setEmail(String eMail){
            this.eMail = eMail;
        }

        public NomineesAdapterViewHolder(View v, Context context, List<Nominee> nominees) {
            super(v);
            this.context = context;
            this.nominees = nominees;

            name = (TextView) v.findViewById(R.id.nom_name);
//            email = (TextView) v.findViewById(R.id.member_email);
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


    }
    public List<Nominee> getMemberList() {
        return mNominees;
    }


}
