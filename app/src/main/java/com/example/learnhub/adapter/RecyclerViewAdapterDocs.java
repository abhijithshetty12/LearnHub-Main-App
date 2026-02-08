package com.example.learnhub.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnhub.R;
import com.example.learnhub.model.DocumentModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

public class RecyclerViewAdapterDocs extends RecyclerView.Adapter<RecyclerViewAdapterDocs.ViewHolder> {
    private Context context;
    List<DocumentModel> documentModelList;

    public RecyclerViewAdapterDocs(Context context, List<DocumentModel> documentModelList) {
        this.context = context;
        this.documentModelList = documentModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_show_document,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      DocumentModel documentModel = documentModelList.get(position);
      holder.filename.setText(documentModel.getFileName());
      Uri uri = documentModel.getFileURI();
      if (isPdf(uri)){
          Bitmap pdfThumbNail = generatePdfThumbnail(uri);
          Glide.with(context).load(pdfThumbNail).into(holder.fileimg);
      }else if (isImage(uri)){
          Glide.with(context)
                  .load(uri)
                  .into(holder.fileimg);
      }else {
      }


      holder.itemView.setOnClickListener(v -> {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setDataAndType(uri, getMimeType(uri));
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          if (intent.resolveActivity(context.getPackageManager()) != null) {
              context.startActivity(intent);
          } else {
              Toast.makeText(context, "No application available to open this document.", Toast.LENGTH_SHORT).show();
          }
      });
    }

    @Override
    public int getItemCount() {
        return documentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       public TextView filename;
       public ImageView fileimg ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            fileimg = itemView.findViewById(R.id.fileimg);
        }

        @Override
        public void onClick(View v) {

        }
    }
    private Bitmap generatePdfThumbnail(Uri pdfUri){
        Bitmap bitmap = null;
        try{
            ParcelFileDescriptor fileDescriptor = context.getContentResolver().openFileDescriptor(pdfUri,"r");
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            if (renderer.getPageCount()>0){
                PdfRenderer.Page page  =renderer.openPage(0);
                bitmap = Bitmap.createBitmap(page.getWidth(),page.getHeight(),Bitmap.Config.ARGB_8888);
                page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
            }
            renderer.close();
            fileDescriptor.close();

        } catch (Exception e) {
           e.printStackTrace();
        }
        return bitmap;
    }
    private String getMimeType(Uri uri){
        String mimeType=null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)){
            ContentResolver contentResolver = context.getContentResolver();
            mimeType = contentResolver.getType(uri);
        }else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        }
        return mimeType;
    }
    private Boolean isPdf(Uri uri){
        String mimeType = getMimeType(uri);
        return  mimeType!=null && mimeType.equals("application/pdf");
    }
    private Boolean isImage(Uri uri){
        String mimeType = getMimeType(uri);
        return mimeType!=null && mimeType.startsWith("image/");
    }

}
