<<<<<<< HEAD:java/src/client/communication/GameHistoryController.java
package client.communication;

import java.util.*;
import java.util.List;

import client.base.*;
import shared.definitions.*;

/**
 * Game history controller implementation
 */
public class GameHistoryController extends Controller implements IGameHistoryController {

    public GameHistoryController(IGameHistoryView view) {

        super(view);

        initFromModel();
    }

    @Override
    public IGameHistoryView getView() {

        return (IGameHistoryView) super.getView();
    }

    private void initFromModel() {

        //<temp>
        List<LogEntry> entries = new ArrayList<LogEntry>();
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));

        getView().setEntries(entries);

        //</temp>
    }

}
=======
package client.communication;

import java.util.*;
import java.util.List;

import client.base.*;
import shared.definitions.*;

/**
 * Game history controller implementation
 */
public class GameHistoryController extends Controller implements IGameHistoryController {

    public GameHistoryController(IGameHistoryView view) {

        super(view);

        initFromModel();
    }

    @Override
    public IGameHistoryView getView() {

        return (IGameHistoryView) super.getView();
    }

    private void initFromModel() {

		//<temp>
        List<LogEntry> entries = new ArrayList<LogEntry>();
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
        entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
        entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));

        getView().setEntries(entries);

        //</temp>
    }

}
>>>>>>> d7ac12c8aed2ad9d15af7283b9f48b2bd6e7f9e2:java/client/communication/GameHistoryController.java