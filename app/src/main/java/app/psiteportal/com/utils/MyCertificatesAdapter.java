package app.psiteportal.com.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.psiteportal.MyCertificatesActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.SeminarProfileActivity;

/**
 * Created by fmpdroid on 8/16/2016.
 */

public class MyCertificatesAdapter extends RecyclerView.Adapter<MyCertificatesAdapter.MyCertificatesViewHolder> {

    private List<Seminar> seminars;
    private Context context;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    static String usertype;
    static int pid;

    public MyCertificatesAdapter(){
    }
    public MyCertificatesAdapter(Context context, List<Seminar> seminars){
        this.context = context;
        this.seminars = seminars;
    }

    @Override
    public int getItemCount() {
        return seminars.size();
    }

    @Override
    public void onBindViewHolder(MyCertificatesViewHolder holder, final int position) {

        Seminar s =  seminars.get(position);
        holder.seminarName.setText(s.getSeminarName());
        holder.date.setText("Date: " + s.getDate());
//        status.setText(Html.fromHtml("<font color='#00CD00'>" + member.getStatus() + "</font>"));
//        holder.cardView.setBackground(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.background_final));
        //holder.cardView.setBackground(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.cardview_border));
    }

    @Override
    public MyCertificatesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.my_certificates_content, viewGroup, false);

        imageLoader = AppController.getInstance().getImageLoader();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "SHIT", Toast.LENGTH_LONG);
            }
        });
        return new MyCertificatesViewHolder(itemView, context, seminars);
    }


    public static class MyCertificatesViewHolder extends RecyclerView.ViewHolder {

        public TextView seminarName;
        public TextView date;
        public ImageButton download_certificate;
        public Seminar seminar;

        List<Seminar> seminars = new ArrayList<>();
        Context context;
        long downloadReference;
        DownloadManager mManager;

        public MyCertificatesViewHolder(View v, Context context, List<Seminar> seminars){
            super(v);
            this.seminars = seminars;
            this.context = context;
//            v.setOnClickListener(this);

            seminarName = (TextView) v.findViewById(R.id.my_seminar_name);
            date = (TextView) v.findViewById(R.id.my_seminar_date);
//            download_certificate = (ImageButton) v.findViewById(R.id.my_seminar_download);
//            download_certificate.setOnClickListener(this);
        }


//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            Seminar s = this.seminars.get(position);
//            showDialog(s.getId(), MyCertificatesActivity.pid, s.getSeminarName(), v);
//        }
//
//        public void showDialog(final String id, final String pid, final String seminarName, View v) {
//            AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
//            alertDialog.setTitle("Download Certificate");
//            alertDialog.setMessage("Are you sure to download this certificate?");
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            download(id, pid, seminarName);
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "No",
//                    new DialogInterface.OnClickListener(){
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
//        }
//
//        public void download(String sid, String pid, String seminarName) {
//            mManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//            String url = Config.ROOT_URL + "downloadTest?sid="+sid+"&pid="+pid;
//            Uri Download_Uri = Uri.parse(url);
//            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.setMimeType("application/pdf");
//            request.allowScanningByMediaScanner();
//            //Restrict the types of networks over which this download may proceed.
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//            //Set whether this download may proceed over a roaming connection.
//            request.setAllowedOverRoaming(false);
//            //Set the title of this download, to be displayed in notifications (if enabled).
//            request.setTitle("Certificate Download");
//            //Set a description of this download, to be displayed in notifications (if enabled)
//            request.setDescription("Download in Progress");
//            //Set the local destination for the downloaded file to a path within the application's external files directory
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, seminarName.replace(" ", "_") + ".pdf");
//
//            //Enqueue a new download and same the referenceId
//            downloadReference = mManager.enqueue(request);
////            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//        }

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadReference);
                    Cursor c = mManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

//                            ImageView view = (ImageView) findViewById(R.id.imageView1);
//                            String uriString = c
//                                    .getString(c
//                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                            view.setImageURI(Uri.parse(uriString));
                        }
                    }
                }
            }
        };
    }

    public List<Seminar> getSeminarsList() {
        return seminars;
    }


    public void getUserId(int userId){
        pid = userId;
    }
    public void getUserType(String userType){
        usertype = userType;
    }

}


