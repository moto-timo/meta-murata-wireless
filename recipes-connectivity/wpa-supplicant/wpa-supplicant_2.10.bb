SUMMARY = "Client for Wi-Fi Protected Access (WPA)"
DESCRIPTION = "wpa_supplicant is a WPA Supplicant for Linux, BSD, Mac OS X, and Windows with support for WPA and WPA2 (IEEE 802.11i / RSN). Supplicant is the IEEE 802.1X/WPA component that is used in the client stations. It implements key negotiation with a WPA Authenticator and it controls the roaming and IEEE 802.11 authentication/association of the wlan driver."
HOMEPAGE = "http://w1.fi/wpa_supplicant/"
BUGTRACKER = "http://w1.fi/security/"
SECTION = "network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5ebcb90236d1ad640558c3d3cd3035df \
                    file://README;beginline=1;endline=56;md5=e3d2f6c2948991e37c1ca4960de84747 \
                    file://wpa_supplicant/wpa_supplicant.c;beginline=1;endline=12;md5=76306a95306fee9a976b0ac1be70f705"

DEPENDS = "dbus libnl"

SRC_URI = "http://w1.fi/releases/wpa_supplicant-${PV}.tar.gz \
           file://wpa-supplicant.sh \
           file://wpa_supplicant.conf \
           file://wpa_supplicant.conf-sane \
           file://99_wpa_supplicant \
           file://0001-build-Re-enable-options-for-libwpa_client.so-and-wpa.patch \
           file://0002-Fix-removal-of-wpa_passphrase-on-make-clean.patch \
           file://0001-Install-wpa_passphrase-when-not-disabled.patch \
           "
SRC_URI += " \
           file://0001-wpa_supplicant-Support-4-way-handshake-offload-for-F.patch;apply=yes \
           file://0002-wpa_supplicant-Notify-Neighbor-Report-for-driver-tri.patch;apply=yes \
           file://0003-nl80211-Report-connection-authorized-in-EVENT_ASSOC.patch;apply=yes \
           file://0004-wpa_supplicant-Add-PMKSA-cache-for-802.1X-4-way-hand.patch;apply=yes \
           file://0005-OpenSSL-Fix-build-with-OpenSSL-1.0.1.patch;apply=yes \
           file://0006-nl80211-Check-SAE-authentication-offload-support.patch;apply=yes \
           file://0007-SAE-Pass-SAE-password-on-connect-for-SAE-authenticat.patch;apply=yes \
           file://0008-nl80211-Support-4-way-handshake-offload-for-WPA-WPA2.patch;apply=yes \
           file://0009-AP-Support-4-way-handshake-offload-for-WPA-WPA2-PSK.patch;apply=yes \
           file://0010-nl80211-Support-SAE-authentication-offload-in-AP-mod.patch;apply=yes \
           file://0011-SAE-Support-SAE-authentication-offload-in-AP-mode.patch;apply=yes \
           file://0012-DPP-Do-more-condition-test-for-AKM-type-DPP-offload.patch;apply=yes \
           file://0013-non-upstream-defconfig_base-Add-Infineon-default-con.patch;apply=yes \
           file://0014-CVE_2019_9501-Fix-to-check-Invalid-GTK-IE-length-in-.patch;apply=yes \
           file://0015-Add-CONFIG_WPA3_SAE_AUTH_EARLY_SET-flags-and-codes-murata.patch;apply=yes \
           file://0016-SAE-Set-the-right-WPA-Versions-for-FT-SAE-key-manage.patch;apply=yes \
           file://0017-wpa_supplicant-Support-WPA_KEY_MGMT_FT-for-eapol-off.patch;apply=yes \
           file://0018-wpa_supplicant-suppress-deauth-for-PMKSA-caching-dis.patch;apply=yes \
           file://0019-Fix-for-PMK-expiration-issue-through-supplicant-murata.patch;apply=yes \
           file://0020-SAE-Drop-PMKSA-cache-after-receiving-specific-deauth.patch;apply=yes \
           file://0022-Avoid-deauthenticating-STA-if-the-reason-for-freeing.patch;apply=yes \
           file://0023-wpa_supplicant-support-bgscan.patch;apply=yes \
           file://0024-non-upstream-wl-cmd-create-interface-to-support-driv-murata.patch;apply=yes \
           file://0025-non-upstream-wl-cmd-create-wl_do_cmd-as-an-entry-doi.patch;apply=yes \
           file://0026-non-upstream-wl-cmd-create-ops-table-to-do-wl-comman.patch;apply=yes \
           file://0027-non-upstream-wl-cmd-add-more-compile-flag.patch;apply=yes \
           file://0028-base-ifx-2.10-Fix-dpp-config-parameter-setting.patch;apply=yes \
           file://0029-base-ifx-2_10-DPP-Resolving-failure-of-dpp-configura.patch;apply=yes \
           file://0030-base-ifx-2.10-Enabling-SUITEB192-and-SUITEB-compile-.patch;apply=yes \
           file://0031-base-ifx-2_10-DPP-Enabling-CLI_EDIT-option-for-enrol.patch;apply=yes \
           file://0032-base-ifx-2_10-P2P-Fixes-Scan-trigger-failed-once-GC-.patch;apply=yes \
           file://0033-non-upstream-SAE-disconnect-after-PMKSA-cache-expire.patch;apply=yes \
           file://0034-Add-support-for-beacon-loss-roaming.patch;apply=yes \
           "
SRC_URI[sha256sum] = "20df7ae5154b3830355f8ab4269123a87affdea59fe74fe9292a91d0d7e17b2f"

S = "${WORKDIR}/wpa_supplicant-${PV}"

inherit pkgconfig systemd

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[gnutls] = ",,gnutls libgcrypt"
PACKAGECONFIG[openssl] = ",,openssl"

CVE_PRODUCT = "wpa_supplicant"

EXTRA_OEMAKE = "'LIBDIR=${libdir}' 'INCDIR=${includedir}' 'BINDIR=${sbindir}'"

do_configure () {
	${MAKE} -C wpa_supplicant clean
	sed -e '/^CONFIG_TLS=/d' <wpa_supplicant/defconfig >wpa_supplicant/.config

	if ${@ bb.utils.contains('PACKAGECONFIG', 'openssl', 'true', 'false', d) }; then
		echo 'CONFIG_TLS=openssl' >>wpa_supplicant/.config
	elif ${@ bb.utils.contains('PACKAGECONFIG', 'gnutls', 'true', 'false', d) }; then
		echo 'CONFIG_TLS=gnutls' >>wpa_supplicant/.config
        sed -i -e 's/\(^CONFIG_DPP=\)/#\1/' \
               -e 's/\(^CONFIG_EAP_PWD=\)/#\1/' \
               -e 's/\(^CONFIG_SAE=\)/#\1/' wpa_supplicant/.config
	fi

	# For rebuild
	rm -f wpa_supplicant/*.d wpa_supplicant/dbus/*.d
}

do_compile () {
	oe_runmake -C wpa_supplicant
	if [ -z "${DISABLE_STATIC}" ]; then
		oe_runmake -C wpa_supplicant libwpa_client.a
	fi
}

do_install () {
	oe_runmake -C wpa_supplicant DESTDIR="${D}" install

	install -d ${D}${docdir}/wpa_supplicant
	install -m 644 wpa_supplicant/README ${WORKDIR}/wpa_supplicant.conf ${D}${docdir}/wpa_supplicant

	install -d ${D}${sysconfdir}
	install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane ${D}${sysconfdir}/wpa_supplicant.conf

	install -d ${D}${sysconfdir}/network/if-pre-up.d/
	install -d ${D}${sysconfdir}/network/if-post-down.d/
	install -d ${D}${sysconfdir}/network/if-down.d/
	install -m 755 ${WORKDIR}/wpa-supplicant.sh ${D}${sysconfdir}/network/if-pre-up.d/wpa-supplicant
	ln -sf ../if-pre-up.d/wpa-supplicant ${D}${sysconfdir}/network/if-post-down.d/wpa-supplicant

	install -d ${D}/${sysconfdir}/dbus-1/system.d
	install -m 644 ${S}/wpa_supplicant/dbus/dbus-wpa_supplicant.conf ${D}/${sysconfdir}/dbus-1/system.d
	install -d ${D}/${datadir}/dbus-1/system-services
	install -m 644 ${S}/wpa_supplicant/dbus/*.service ${D}/${datadir}/dbus-1/system-services

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}/${systemd_system_unitdir}
		install -m 644 ${S}/wpa_supplicant/systemd/*.service ${D}/${systemd_system_unitdir}
	fi

	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_wpa_supplicant ${D}/etc/default/volatiles

	install -d ${D}${includedir}
	install -m 0644 ${S}/src/common/wpa_ctrl.h ${D}${includedir}

	if [ -z "${DISABLE_STATIC}" ]; then
		install -d ${D}${libdir}
		install -m 0644 wpa_supplicant/libwpa_client.a ${D}${libdir}
	fi
}

do_install:append () {
        echo "Compiling: "
        echo "ARCH: ${ARCH} "
        install -d ${D}${sbindir}
        install -m 755 wpa_supplicant/wpa_supplicant ${D}${sbindir}/wpa_supplicant.cyw
        install -m 755 wpa_supplicant/wpa_cli ${D}${sbindir}/wpa_cli.cyw
}

pkg_postinst:${PN} () {
	# If we're offline, we don't need to do this.
	if [ "x$D" = "x" ]; then
		killall -q -HUP dbus-daemon || true
	fi
}

PACKAGE_BEFORE_PN += "${PN}-passphrase ${PN}-cli"
PACKAGES =+ "${PN}-lib"
PACKAGES += "${PN}-plugins"
ALLOW_EMPTY:${PN}-plugins = "1"

PACKAGES_DYNAMIC += "^${PN}-plugin-.*$"
NOAUTOPACKAGEDEBUG = "1"

FILES:${PN}-passphrase = "${sbindir}/wpa_passphrase"
FILES:${PN}-cli = "${sbindir}/wpa_cli"
FILES:${PN}-lib = "${libdir}/libwpa_client*${SOLIBSDEV}"
FILES:${PN} += "${datadir}/dbus-1/system-services/* ${systemd_system_unitdir}/*"
FILES:${PN}-dbg += "${sbindir}/.debug ${libdir}/.debug"

CONFFILES:${PN} += "${sysconfdir}/wpa_supplicant.conf"

RRECOMMENDS:${PN} = "${PN}-passphrase ${PN}-cli ${PN}-plugins"

SYSTEMD_SERVICE:${PN} = "wpa_supplicant.service"
SYSTEMD_AUTO_ENABLE = "disable"

python split_wpa_supplicant_libs () {
    libdir = d.expand('${libdir}/wpa_supplicant')
    dbglibdir = os.path.join(libdir, '.debug')

    split_packages = do_split_packages(d, libdir, r'^(.*)\.so', '${PN}-plugin-%s', 'wpa_supplicant %s plugin', prepend=True)
    split_dbg_packages = do_split_packages(d, dbglibdir, r'^(.*)\.so', '${PN}-plugin-%s-dbg', 'wpa_supplicant %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')

    if split_packages:
        pn = d.getVar('PN')
        d.setVar('RRECOMMENDS:' + pn + '-plugins', ' '.join(split_packages))
        d.appendVar('RRECOMMENDS:' + pn + '-dbg', ' ' + ' '.join(split_dbg_packages))
}
PACKAGESPLITFUNCS:prepend = "split_wpa_supplicant_libs "
