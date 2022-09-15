package mage.cards.m;

import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.continuous.GainAbilityAllEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.counters.CounterType;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.events.EntersTheBattlefieldEvent;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;

import java.util.UUID;

public class MasterChef extends CardImpl{

    public MasterChef(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{2}{G}");

        this.addSuperType(SuperType.LEGENDARY);
        this.subtype.add(SubType.BACKGROUND);

        // Commander creatures you own have “This creature enters the battlefield with an additional +1/+1 counter on it” 
        // and “Other creatures you control enter the battlefield with an additional +1/+1 counter on them.”
        this.addAbility(new SimpleStaticAbility(new GainAbilityAllEffect(
            new EntersBattlefieldAbility(
                new AddCountersSourceEffect(CounterType.P1P1.createInstance()).setText("with and additional +1/+1 counter on it")
            ), Duration.WhileOnBattlefield, StaticFilters.FILTER_CREATURES_OWNED_COMMANDER, "Commander creatures")));
            
        this.addAbility(new SimpleStaticAbility(new GainAbilityAllEffect(
            new SimpleStaticAbility(
                new MasterChefReplacementEffect()
            ), Duration.WhileOnBattlefield, StaticFilters.FILTER_CREATURES_OWNED_COMMANDER)));

    }

    private MasterChef(final MasterChef card) {
        super(card);
    }

    @Override
    public MasterChef copy() {
        return new MasterChef(this);
    }
    
}

class MasterChefReplacementEffect extends ReplacementEffectImpl {

    MasterChefReplacementEffect() {
        super(Duration.WhileOnBattlefield, Outcome.BoostCreature);
        staticText = "Other Creatures you control enter the " +
                "battlefield with an additional +1/+1 counter on it";
    }

    private MasterChefReplacementEffect(final MasterChefReplacementEffect effect) {
        super(effect);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Permanent creature = ((EntersTheBattlefieldEvent) event).getTarget();
        if (creature != null) {
            creature.addCounters(CounterType.P1P1.createInstance(), source.getControllerId(), source, game, event.getAppliedEffects());
        }
        return false;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ENTERS_THE_BATTLEFIELD;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        Permanent creature = ((EntersTheBattlefieldEvent)event).getTarget();
        return creature != null
                && creature.isCreature()
                && creature.isControlledBy(source.getControllerId())
                && !source.getSourceId().equals(creature.getId());
    }

    @Override
    public MasterChefReplacementEffect copy() {
        return new MasterChefReplacementEffect(this);
    }

    
}