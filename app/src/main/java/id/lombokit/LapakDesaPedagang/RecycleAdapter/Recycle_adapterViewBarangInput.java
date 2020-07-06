package id.lombokit.LapakDesaPedagang.RecycleAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.Locale;

import id.lombokit.LapakDesaPedagang.Models.Categories;
import id.lombokit.LapakDesaPedagang.R;
import id.lombokit.LapakDesaPedagang.Root_url.EndPoints;
import id.lombokit.LapakDesaPedagang.Utilities;

public class Recycle_adapterViewBarangInput extends RecyclerView.Adapter<Recycle_adapterViewBarangInput.My_viewHolder> {
    private Context context;
    private Categories categories;
    private  onDeleteBarang onDeleteBarang;
    private  onEditBarang onEditBarang;


    public Recycle_adapterViewBarangInput(Context context, Categories categories,onDeleteBarang onDeleteBarang,onEditBarang onEditBarang) {
        this.context = context;
        this.categories = categories;
        this.onDeleteBarang = onDeleteBarang;
        this.onEditBarang = onEditBarang;
    }

    @NonNull
    @Override
    public Recycle_adapterViewBarangInput.My_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_product, parent, false);
        return new Recycle_adapterViewBarangInput.My_viewHolder(viewItem);
    }

    public class My_viewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewImage, delete_barang, editBarang;
        TextView textViewTitle;
        TextView textViewHarga;
        TextView textViewSatuan;
        TextView textViewstok;
        TextView textViewStatus;


        public My_viewHolder(@NonNull View itemView) {
            super(itemView);
            delete_barang = itemView.findViewById(R.id.delete_barang);
            editBarang = itemView.findViewById(R.id.edit_barang);
            imageViewImage = itemView.findViewById(R.id.gambar);
            textViewTitle = itemView.findViewById(R.id.nama_barang);
            textViewHarga = itemView.findViewById(R.id.harga);
            textViewSatuan = itemView.findViewById(R.id.satuan);
            textViewstok = itemView.findViewById(R.id.stok);
            textViewStatus = itemView.findViewById(R.id.status);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull Recycle_adapterViewBarangInput.My_viewHolder holder, final int position) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(localeID);
        final int parse = Integer.parseInt(categories.getProductsArrayList().get(position).getHarga());
        holder.textViewTitle.setText(Utilities.toTitleCase(categories.getProductsArrayList().get(position).getNama_barang()));
        holder.textViewHarga.setText(rupiahFormat.format((double) parse));

        holder.textViewSatuan.setText("/" + categories.getProductsArrayList().get(position).getSatuan());
        holder.textViewstok.setText("" + categories.getProductsArrayList().get(position).getStok());
        holder.textViewStatus.setText(categories.getProductsArrayList().get(position).getStatus());

        Glide.with(context).load(EndPoints.ROOT_URL2 + "upload/barang/" + categories.getProductsArrayList().get(position).getgambar()).into(holder.imageViewImage);

        holder.delete_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDeleteBarang.deleteItem(categories.getProductsArrayList().get(position).getId_barang());
            }
        });
        holder.editBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditBarang.editItem();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.productsArrayList.size();
    }


    public  interface onDeleteBarang{
        void deleteItem(int id_barang);
    }
    public  interface onEditBarang{
        void editItem();
    }
}
