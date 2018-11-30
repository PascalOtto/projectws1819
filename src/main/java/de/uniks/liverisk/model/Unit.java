package de.uniks.liverisk.model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Unit 
{

   public static final String PROPERTY_platform = "platform";

   private Platform platform = null;

   public Platform getPlatform()
   {
      return this.platform;
   }

   public Unit setPlatform(Platform value)
   {
      if (this.platform != value)
      {
         Platform oldValue = this.platform;
         if (this.platform != null)
         {
            this.platform = null;
            oldValue.withoutUnits(this);
         }
         this.platform = value;
         if (value != null)
         {
            value.withUnits(this);
         }
         firePropertyChange("platform", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_player = "player";

   private Player player = null;

   public Player getPlayer()
   {
      return this.player;
   }

   public Unit setPlayer(Player value)
   {
      if (this.player != value)
      {
         Player oldValue = this.player;
         if (this.player != null)
         {
            this.player = null;
            oldValue.withoutUnits(this);
         }
         this.player = value;
         if (value != null)
         {
            value.withUnits(this);
         }
         firePropertyChange("player", oldValue, value);
      }
      return this;
   }



   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }



   public void removeYou()
   {
      this.setPlatform(null);
      this.setPlayer(null);

   }


}