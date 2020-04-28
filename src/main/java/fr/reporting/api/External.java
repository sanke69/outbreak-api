package fr.reporting.api;

import fr.geodesic.referential.api.countries.Country;

public interface External {

	public interface Position { }
	public interface GeoPosition extends Position { public double getLongitude(); public double getLatitude(); }

	public interface Address  { }

	public interface Location {

//		GeoPosition getPosition();
//		Address     getAddress();

//		String      getCity();
//		String      getRegion();
		Country     getCountry();

//		Locale      getLocale();

	}

}
