package client.discard;

import shared.definitions.*;
import client.base.*;
import client.misc.*;
import client.model.ModelFacade;
import client.model.Populator;

import java.util.Observable;
import java.util.Observer;

/**
 * Discard controller implementation
 */
public class DiscardController extends Controller 
    implements IDiscardController, Observer {

    private IWaitView waitView;

    /**
     * DiscardController constructor
     *
     * @param view View displayed to let the user select cards to discard
     * @param waitView View displayed to notify the user that they are waiting
     * for other players to discard
     */
    public DiscardController(IDiscardView view, IWaitView waitView) {

        super(view);
        Populator.getInstance().addObserver(this);
        this.waitView = waitView;
    }

    public IDiscardView getDiscardView() {
        return (IDiscardView) super.getView();
    }

    public IWaitView getWaitView() {
        return waitView;
    }

    @Override
    public void increaseAmount(ResourceType resource) {

    }

    @Override
    public void decreaseAmount(ResourceType resource) {

    }

    @Override
    public void discard() {

        getDiscardView().closeModal();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Populator && arg instanceof ModelFacade) {
            
        }
    }

}
