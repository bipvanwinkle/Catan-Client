package shared.models;

import shared.definitions.DevCardType;
import shared.exceptions.ExcessiveDevCards;
import shared.models.DTO.DevCardListDTO;

public class DevCardList {

    private int monopoly;
    private int roadBuilding;
    private int yearOfPlenty;
    private int monument;
    private int soldier;

    private static int MAX_SOLDIER = 14;
    private static int MAX_MONUMENT = 5;
    private static int MAX_PROGRESS = 2;

    public DevCardList() {
        // TODO Auto-generated constructor stub
    }

    public DevCardList(DevCardListDTO devCards) {
        this.monopoly = devCards.getMonopoly();
        this.roadBuilding = devCards.getRoadBuilding();
        this.yearOfPlenty = devCards.getYearOfPlenty();
        this.monument = devCards.getMonument();
        this.soldier = devCards.getSoldier();
    }

    /**
     * This function is used to add dev cards to the list
     *
     * @param type the type of resource
     * @param amount the number of cards
     */
    public void addCards(DevCardType type, int amount) {

    }

    /**
     * This function is used to remove dev cards from the list
     *
     * @pre the type and amount of resources needs to be available in the list
     * before they can be removed
     * @param type the type of resource
     * @param amount the amount of resources
     */
    public void removeCards(DevCardType type, int amount) {

    }

    /**
     * This function will return the number of cards of a specified dev card
     * type in the <code>DevCardList</code>
     *
     * @param type type of resource to check
     * @return the number of cards in the list
     */
    public int getResourceNumber(DevCardType type) {
        return 0;
    }

    public int getMonopoly() {
        return monopoly;
    }

    public void setMonopoly(int monopoly) throws ExcessiveDevCards {
        if (monopoly > MAX_PROGRESS) {
            throw new ExcessiveDevCards();
        }
        this.monopoly = monopoly;
    }

    public int getRoadBuilding() {
        return roadBuilding;
    }

    public void setRoadBuilding(int roadBuilding) throws ExcessiveDevCards {
        if (roadBuilding > MAX_PROGRESS) {
            throw new ExcessiveDevCards();
        }
        this.roadBuilding = roadBuilding;
    }

    public int getYearOfPlenty() {
        return yearOfPlenty;
    }

    public void setYearOfPlenty(int yearOfPlenty) throws ExcessiveDevCards {
        if (yearOfPlenty > MAX_PROGRESS) {
            throw new ExcessiveDevCards();
        }
        this.yearOfPlenty = yearOfPlenty;
    }

    public int getMonument() {
        return monument;
    }

    public void setMonument(int monument) throws ExcessiveDevCards {
        if (monument > MAX_MONUMENT) {
            throw new ExcessiveDevCards();
        }
        this.monument = monument;
    }

    public int getSoldier() {
        return soldier;
//    	return 1;
    }

    public void setSoldier(int soldier) throws ExcessiveDevCards {
        if (soldier > MAX_SOLDIER) {
            throw new ExcessiveDevCards();
        }
        this.soldier = soldier;
    }

    public int getNumDevCards() {
        return monopoly + roadBuilding + yearOfPlenty + soldier + monument;
    }

}
