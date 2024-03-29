package de.svbrockscheid.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;

import de.svbrockscheid.R;
import de.svbrockscheid.model.InfoNachricht;
import de.svbrockscheid.model.UpdateInfo;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

/**
 * Created by Matthias on 31.12.2014.
 */
public class NachrichtenAdapter extends RecyclerView.Adapter<NachrichtenHolder> {

    private static final DateFormat SIMPLE_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private UpdateInfo updateInfo;

    private CursorList<InfoNachricht> nachrichten;

    public NachrichtenAdapter() {
        super();
        //alle Nachrichten aus der Datenbank abrufen
        refresh();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case 0:
                return 0;
            case 1:
                InfoNachricht infoNachricht = nachrichten.get(position - (updateInfo != null ? 1 : 0));
                if (infoNachricht != null) {
                    return infoNachricht.getId();
                }
            default:
                return -1;
        }
    }

    public void setUpdateInfo(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
        notifyDataSetChanged();
    }

    public void refresh() {
        //alle nachrichten updaten
        disconnect();
        nachrichten = Query.all(InfoNachricht.class).get();
        notifyDataSetChanged();
    }

    public void disconnect() {
        //die DB-Verbindung schließen
        if (nachrichten != null) {
            nachrichten.close();
            nachrichten = null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (updateInfo != null && position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public NachrichtenHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            return new UpdateInfoHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_update, viewGroup, false));
        } else {
            return new NachrichtenHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_nachrichten, viewGroup, false));
        }
    }

    @Override
    public int getItemCount() {
        return (nachrichten == null ? 0 : nachrichten.size()) + (updateInfo == null ? 0 : 1);
    }

    @Override
    public void onBindViewHolder(NachrichtenHolder nachrichtenHolder, int position) {
        if (updateInfo != null) {
            position--;
        }
        if (position < 0) {
            //show the update information
            nachrichtenHolder.nachricht.setText(R.string.update_verfuegbar);
            ((UpdateInfoHolder) nachrichtenHolder).changeLog.setText(updateInfo.changeLog);
            ((UpdateInfoHolder) nachrichtenHolder).downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //launch the download intent
                    Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.url));
                    v.getContext().startActivity(downloadIntent);
                }
            });
        } else {
            InfoNachricht infoNachricht = nachrichten.get(position);
            nachrichtenHolder.nachricht.setText(infoNachricht.getNachricht());
            nachrichtenHolder.zeit.setText(SIMPLE_DATE_FORMAT.format(infoNachricht.getZeit()));
        }
    }


}
