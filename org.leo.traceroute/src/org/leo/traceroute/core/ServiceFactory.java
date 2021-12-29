/**
 * Open Visual Trace Route
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.leo.traceroute.core;

import org.leo.traceroute.core.autocomplete.AutoCompleteProvider;
import org.leo.traceroute.core.geo.GeoService;
import org.leo.traceroute.core.network.*;
import org.leo.traceroute.core.route.ITraceRoute;
import org.leo.traceroute.core.route.impl.OSTraceRoute;
import org.leo.traceroute.core.sniffer.IPacketsSniffer;
import org.leo.traceroute.core.sniffer.impl.EmptyPacketsSniffer;
import org.leo.traceroute.core.whois.WhoIs;
import org.leo.traceroute.install.Env;
import org.leo.traceroute.ui.TraceRouteFrame;
import org.leo.traceroute.ui.util.SplashScreen;

import javax.swing.*;
import java.util.Arrays;

/**
 * ServiceFactory $Id: ServiceFactory.java 222 2016-01-09 19:19:33Z leolewis $
 * <pre>
 * </pre>
 * @author Leo Lewis
 */
public class ServiceFactory {

	public enum Mode {
		TRACE_ROUTE,
		SNIFFER,
		WHOIS
	}

	private final ITraceRoute _traceroute;
	private final IPacketsSniffer _sniffer;

	private final INetworkService<?> _networkService;

	private final DNSLookupService _dnsLookup;
	private final WhoIs _whois;

	private final GeoService _geo;

	private final AutoCompleteProvider _autocomplete;

	private final SplashScreen _splash;
	private TraceRouteFrame _main;

	public ServiceFactory(final ITraceRoute traceroute, final IPacketsSniffer sniffer, final INetworkService<?> networkService,
			final DNSLookupService dnsLookup, final GeoService geo, final AutoCompleteProvider autoComplete, final WhoIs whois) {
		super();
		_traceroute = traceroute;
		_sniffer = sniffer;
		_networkService = networkService;
		_dnsLookup = dnsLookup;
		_geo = geo;
		_autocomplete = autoComplete;
		_whois = whois;
		_splash = null;
	}

	/**
	 * Constructor
	 */
	public ServiceFactory(final SplashScreen splash, TraceRouteFrame main) {
		_splash = splash;
		_main = main;

		_networkService = new EmptyNetworkService();

		_traceroute = new OSTraceRoute();
		_sniffer = new EmptyPacketsSniffer();
		_dnsLookup = new DNSLookupService();
		_geo = new GeoService();
		_autocomplete = new AutoCompleteProvider();
		_whois = new WhoIs();
	}

	public void init() throws Exception {
		_dnsLookup.init(this);
		_geo.init(this);
		_networkService.init(this);
		_traceroute.init(this);
		_sniffer.init(this);
		_autocomplete.init(this);
		_whois.init(this);
		Arrays.asList(Mode.values()).forEach(_networkService::notifyInterface);
		if (!isEmbeddedTRAvailable()) {
			Env.INSTANCE.setUseOSTraceroute(true);
		} else {
			_networkService.setCurrentNetworkDevice(Mode.TRACE_ROUTE, Env.INSTANCE.getTrInterfaceIndex());
		}
		if (isSnifferAvailable()) {
			_networkService.setCurrentNetworkDevice(Mode.SNIFFER, Env.INSTANCE.getSnifferInterfaceIndex());
		}
	}

	/**
	 * Dispose services
	 */
	public void dispose() {
		_networkService.dispose();
		_dnsLookup.dispose();
		_geo.dispose();
		_traceroute.dispose();
		_sniffer.dispose();
		_autocomplete.dispose();
		_whois.dispose();
	}

	/**
	 * Return the value of the field dnsLookup
	 * @return the value of dnsLookup
	 */
	public DNSLookupService getDnsLookup() {
		return _dnsLookup;
	}

	/**
	 * Return the value of the field geo
	 * @return the value of geo
	 */
	public GeoService getGeo() {
		return _geo;
	}

	/**
	 * Return the value of the field traceroute
	 * @return the value of traceroute
	 */
	public ITraceRoute getTraceroute() {
		return _traceroute;
	}

	/**
	 * Return the value of the field sniffer
	 * @return the value of sniffer
	 */
	public IPacketsSniffer getSniffer() {
		return _sniffer;
	}

	/**
	 * Return the value of the field networkService
	 */
	public INetworkService<?> getNetworkService() {
		return _networkService;
	}

	/**
	 * Tell if the sniffer mode is available
	 * @return
	 */
	public boolean isSnifferAvailable() {
		return !_networkService.getNetworkDevices(Mode.SNIFFER).isEmpty();
	}

	/**
	 * Tell if the embedded TR mode is available
	 * @return
	 */
	public boolean isEmbeddedTRAvailable() {
		return !_networkService.getNetworkDevices(Mode.TRACE_ROUTE).isEmpty();
	}

	/**
	 * Return the value of the field autocomplete
	 * @return the value of autocomplete
	 */
	public AutoCompleteProvider getAutocomplete() {
		return _autocomplete;
	}

	/**
	 * Return the value of the field whois
	 * @return the value of whois
	 */
	public WhoIs getWhois() {
		return _whois;
	}

	public SplashScreen getSplash() {
		return _splash;
	}

	/**
	 * @param labelKey
	 * @param incStep
	 */
	public void updateStartup(final String labelKey, final boolean incStep) {
		if (_splash != null) {
			_splash.updateStartup(labelKey, incStep);
		}
	}

	public JFrame getMain() {
		return _main;
	}
}
