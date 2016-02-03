import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BaseClass {
    private PropertyChangeSupport _changes = new PropertyChangeSupport(this);

    /**
     * Fügt einen neuen PropertyChangeListener zur Klasse hinzu
     * @param l PropertyChangeListener
     */
    public void AddPropertyChangeListener(PropertyChangeListener l) {
        _changes.addPropertyChangeListener(l);
    }
    public void RemovePropertyChangeListener(PropertyChangeListener l) {
        _changes.removePropertyChangeListener(l);
    }

    /**
     * Feuert ein PropertyChangeEvent ab, alle PropertyChangeListener werden über ein veränderten Wert informiert.
     * @param propertyName Name des geänderten Property
     * @param oldValue Alter Wert
     * @param newValue Neuer Wert
     */
    public void RaisePropertyChanged(String propertyName, Object oldValue, Object newValue){
        _changes.firePropertyChange(propertyName, oldValue, newValue);
    }
}