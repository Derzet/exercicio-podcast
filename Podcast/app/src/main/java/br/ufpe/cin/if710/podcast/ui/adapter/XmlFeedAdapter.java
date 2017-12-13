package br.ufpe.cin.if710.podcast.ui.adapter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.services.PodcastIntentService;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;
import br.ufpe.cin.if710.podcast.ui.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.lang.String.valueOf;

public class XmlFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;
    Context contextA;
    MainActivity mainActivity;

    public XmlFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        contextA = context;
        linkResource = resource;


    }

    public void setMainActivity(MainActivity callback){
        mainActivity = callback;
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>7
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.itemlista, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.item_title);
		textView.setText(items.get(position).getTitle());
	    return rowView;
	}
	/**/

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button download;
        int position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            holder.download = (Button) convertView.findViewById(R.id.item_action);
            convertView.setTag(holder); //seta para atualizar a medida que desce
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item_title.setText(getItem(position).getTitle());
        holder.item_date.setText(getItem(position).getPubDate());


        holder.position =  position;

         //recuperando estado do botão
        ContentResolver cr = contextA.getContentResolver();
            Cursor c = cr.query(PodcastProviderContract.EPISODE_LIST_URI,
                    PodcastProviderContract.ALL_COLUMNS,
                    valueOf(holder.position+1),
                    null,
                    null);
            c.moveToFirst();
            String state = c.getString(c.getColumnIndex("state"));
            c.close();
            holder.download.setText(state);

        holder.item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemFeed item = getItem(holder.position);
                Intent i = new Intent(contextA,EpisodeDetailActivity.class);
                i.putExtra("itemFeed", item);
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                contextA.startActivity(i); //getAplicationContext
                //https://stackoverflow.com/questions/4197135/how-to-start-activity-in-adapter
                //mainActivity.getTime(v);
                   }
            }

        );


        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String estado = holder.download.getText().toString();
                //incializar o service
                if ((ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    mainActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                           102);
                } else {
                    //CASO TENHA PERMISSÃO,PODERÁ EXECUTAR ESTAS TAREFAS
                    Intent serviceI = new Intent(contextA, PodcastIntentService.class);
                    if (estado.equalsIgnoreCase("BAIXAR")) {
                        serviceI.putExtra("itemFeed", getItem(holder.position));
                        serviceI.putExtra("position",((Integer)(holder.position)).toString());
                        serviceI.setAction("DOWNLOAD");
                        contextA.startService(serviceI);
                        Toast.makeText(contextA,"DOWNLOAD INICIALIZADO!",Toast.LENGTH_LONG).show();
                        holder.download.setEnabled(false); //DESABILITA-LO
                    }else if(estado.equalsIgnoreCase("PLAY")){

                        mainActivity.getPodcastService().playPodcast((holder.position));


                    }else if(estado.equalsIgnoreCase("RESUME")){
                        mainActivity.getPodcastService().resumePodcast((holder.position));

                    } else if(estado.equalsIgnoreCase("PAUSE")){
                        mainActivity.getPodcastService().pausePodcast((holder.position));
                    }
                }
            }
        });
        return convertView;
    }






}