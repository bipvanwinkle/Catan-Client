package shared.models;

import shared.definitions.DevCardType;
import shared.definitions.ResourceType;
import shared.models.DTO.DevCardListDTO;
import shared.models.DTO.ResourceListDTO;
import shared.models.DTO.params.MaritimeTrade;

public class PlayerHand {

    private ResourceList resources;
    private DevCardList devCards;

    public PlayerHand() {
        // TODO Auto-generated constructor stub
    }

    public PlayerHand(ResourceListDTO resourceList, DevCardListDTO devCardList) {
        resources = new ResourceList(resourceList);
        setDevCards(new DevCardList(devCardList));
    }

    /**
     * This function is used to determine if a player has enough resources to
     * buy an object (road settlement, city) I don't currently know what object
     * would store the build requirements for each object, but that would be
     * passed in as a parameter
     *
     * @return whether the object passed in can be built.
     */
    public boolean canBuy() {
        /**
         * I'm assuming that there will be objects that have requirements on
         * what resources are needed to buy each object and that will be passed
         * in
         */
        return false;
    }

    /**
     * This function is used to buy an object I don't currently know what object
     * would store the build requirements for each object, but that would be
     * passed in as a parameter. If the requirements are met, the number of
     * resources would be removed from <code>resources</code>
     */
    public void buy() {

    }

    /**
     * This function is used to determine if the player can buy a dev card
     *
     * @param buyDevCard
     *
     * @return returns true if the player can, false otherwise
     */
    public boolean canBuyDevCard() {
        return resources.getResourceNumber(ResourceType.WHEAT) > 0
                && resources.getResourceNumber(ResourceType.ORE) > 0
                && resources.getResourceNumber(ResourceType.SHEEP) > 0;
    }

    /**
     * This function is used to buy a dev card
     *
     * @pre <code>canBuyDevCard()</code> must return true
     */
    public void buyDevCard() {

    }

    /**
     * This function is used to determine if a dev card can be played
     *
     * @pre User must own dev card in order to play it
     * @param type the type of dev card
     * @return true if player owns dev card
     */
    public boolean canUseDevCard(DevCardType type) {
        return true;
    }

    /**
     * this function is used when a player plays a dev card to decrement the
     * type of dev card owned by the player
     *
     * @pre the function <code>canUseDevCard(DevCardType)</code> must return
     * true
     * @param type type of dev card played
     */
    public void useDevCard(DevCardType type) {

    }

    public boolean CanUpdateResourceCards(ResourceListDTO resourceList) {
        return (resources.getResourceNumber(ResourceType.BRICK)
                + resourceList.getBrick() >= 0
                && resources.getResourceNumber(ResourceType.SHEEP)
                + resourceList.getSheep() >= 0
                && resources.getResourceNumber(ResourceType.WOOD)
                + resourceList.getWood() >= 0
                && resources.getResourceNumber(ResourceType.ORE)
                + resourceList.getOre() >= 0
                && resources.getResourceNumber(ResourceType.WHEAT)
                + resourceList.getWheat() >= 0);
    }

    public int getNumResourceCards() {
        return resources.getResourceNumber(ResourceType.BRICK)
                + resources.getResourceNumber(ResourceType.SHEEP)
                + resources.getResourceNumber(ResourceType.WOOD)
                + resources.getResourceNumber(ResourceType.ORE)
                + resources.getResourceNumber(ResourceType.WHEAT);
    }

    public DevCardList getDevCards() {
        return devCards;
    }

    public void setDevCards(DevCardList devCards) {
        this.devCards = devCards;
    }

    public boolean canMTrade(MaritimeTrade maritimeTrade) {
        return resources.getResourceNumber(maritimeTrade.getInputResource()) >= maritimeTrade.getRatio();
    }

    public boolean canBuildRoad() {
        return resources.getResourceNumber(ResourceType.BRICK) > 0
                && resources.getResourceNumber(ResourceType.WOOD) > 0;
    }

    public boolean canBuildSettlement() {
        return resources.getResourceNumber(ResourceType.BRICK) > 0
                && resources.getResourceNumber(ResourceType.WOOD) > 0
                && resources.getResourceNumber(ResourceType.SHEEP) > 0
                && resources.getResourceNumber(ResourceType.WHEAT) > 0;
    }

    public boolean canBuildCity() {
        return resources.getResourceNumber(ResourceType.WHEAT) >= 2
                && resources.getResourceNumber(ResourceType.ORE) >= 3;
    }

	public int getResourceCount(ResourceType type) {
		return resources.getResourceNumber(type);
	}


}
