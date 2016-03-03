import java.awt.Color;
import javax.swing.*;

/**
 * 
 * @author Armand Ghaffarpour
 * 
 * This class describes a card in the game memory. It has three states: HIDDEN,
 * VISIBLE or MISSING.
 *
 */
public class Card extends JColorfulButton {
    public enum Status {
        HIDDEN, VISIBLE, MISSING
    }

    private Icon icon;
    private Status status;

    public Card(Icon icon) {
        this(icon, Status.MISSING);
    }

    public Card(Icon icon, Status status) {
        this.icon = icon;
        setStatus(status);
    }

    /**
     * Sets the status for the card and changes its appearance after the new
     * status. The card can be either HIDDEN, MISSING or VISIBLE. HIDDEN will set the
     * background blue, MISSING will set the background white and VISIBLE will
     * set the card to show the icon.
     * @param status The new status that the card will have.
     */
    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.HIDDEN) {
            setBackground(Color.blue);
            setIcon(null);
        }
        else if (status == Status.MISSING) {
            setBackground(Color.white);
            setIcon(null);
        }
        else if (status == status.VISIBLE) {
            setBackground(Color.blue);
            setIcon(icon);
        }
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Copies this card and create a new one with the same icon and status.
     * @return a copy of this card.
     */
    public Card copy() {
        return new Card(icon, status);
    }

    /**
     * Compares the two cards icons to see if they are the same.
     * @param card The card to be compared with.
     * @return True if they have the same icon, false if not.
     */
    public boolean sammaBild(Card card) {
        return this.getIcon() == card.getIcon();
    }
}
