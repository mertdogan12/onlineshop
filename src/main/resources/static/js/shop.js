'use strict';

// ---------- Konfiguration ----------

// Alle Endpunkte des Backends – die API-Ansicht zeigt, welche davon schon aufgerufen wurden.
const ENDPOINTS = [
    ['GET', '/test', 'Backend-Status prüfen'],
    ['GET', '/produkte', 'Alle Produkte laden'],
    ['POST', '/produkte', 'Produkt anlegen'],
    ['PUT', '/produkte/{id}', 'Produkt ändern'],
    ['DELETE', '/produkte/{id}', 'Produkt löschen'],
    ['PUT', '/produkt/{id}/addkategorie', 'Kategorien zuordnen'],
    ['PUT', '/produkt/{id}/removekategorie', 'Kategorien entfernen'],
    ['GET', '/kategorie', 'Alle Kategorien laden'],
    ['POST', '/kategorie', 'Kategorie anlegen'],
    ['DELETE', '/kategorie/{id}', 'Kategorie löschen'],
    ['GET', '/kunden', 'Alle Kunden laden'],
    ['POST', '/kunden', 'Kunde mit Profil anlegen'],
    ['DELETE', '/kunden/{id}', 'Kunde löschen'],
    ['GET', '/kunde/bestellungen/{id}', 'Bestellungen eines Kunden'],
    ['GET', '/kundenprofile', 'Alle Kundenprofile laden'],
    ['POST', '/kundenprofile', 'Kundenprofil anlegen'],
    ['POST', '/bestellung/bestellen', 'Bestellung aufgeben'],
    ['PUT', '/bestellung/status/{id}', 'Bestellstatus ändern'],
].map(([method, path, zweck]) => ({
    method,
    path,
    zweck,
    regex: new RegExp('^' + path.replace('{id}', '\\d+') + '$'),
    aufrufe: 0,
    letzterStatus: null,
}));

const STATUS = {
    PENDING: 'Offen',
    BESTELLT: 'Bestellt',
    ABGESCHLOSSEN: 'Abgeschlossen',
    ABGELEHNT: 'Abgelehnt',
    ABGEBROCHEN: 'Abgebrochen',
};

const LIEFERARTEN = [
    { label: 'Standardversand', kosten: 4.95 },
    { label: 'Expressversand', kosten: 9.95 },
    { label: 'Abholung', kosten: 0 },
];

const state = {
    produkte: [],
    kategorien: [],
    kunden: [],
    kundenprofile: [],
    filterKategorie: null,
    suche: '',
    kundeId: null,
    cart: {},
    log: [],
    view: 'shop',
};

// ---------- Hilfsfunktionen ----------

const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
const euro = new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' });

function esc(value) {
    return String(value ?? '').replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
    }[c]));
}

function ladeLokal(key, fallback) {
    try {
        const value = localStorage.getItem(key);
        return value ? JSON.parse(value) : fallback;
    } catch {
        return fallback;
    }
}

function speichereLokal(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value));
    } catch {
        // Ohne localStorage geht der Warenkorb beim Neuladen verloren – sonst kein Problem.
    }
}

// java.sql.Date kommt je nach Jackson-Einstellung als "yyyy-MM-dd" oder als Zeitstempel.
function datum(value) {
    if (value == null || value === '') return '–';
    const d = typeof value === 'number' ? new Date(value) : new Date(`${value}`.slice(0, 10) + 'T00:00:00');
    return isNaN(d) ? String(value) : d.toLocaleDateString('de-DE');
}

function kundeName(kunde) {
    const profil = kunde && kunde.kundenprofil;
    if (profil && (profil.vorname || profil.name)) {
        return `${profil.vorname ?? ''} ${profil.name ?? ''}`.trim();
    }
    return `Kunde #${kunde.id}`;
}

function adresseText(adresse) {
    if (!adresse) return '–';
    return `${adresse.plz || ''} ${adresse.ort || ''}, ${adresse.land || ''}`.trim();
}

function aktuellerKunde() {
    return state.kunden.find(k => k.id === state.kundeId) || null;
}

function produktFarbe(id) {
    return (id * 47) % 360;
}

function initiale(name) {
    return esc((name || '?').charAt(0).toUpperCase());
}

function leer(titel, text = '') {
    return `<div class="leer"><strong>${esc(titel)}</strong><span>${esc(text)}</span></div>`;
}

function toast(text, art = 'ok') {
    const el = document.createElement('div');
    el.className = `toast toast-${art}`;
    el.textContent = text;
    $('#toasts').append(el);
    setTimeout(() => el.classList.add('weg'), 3800);
    setTimeout(() => el.remove(), 4200);
}

// Führt eine Aktion aus und zeigt Fehler als Toast an. Gibt bei Fehler undefined zurück.
async function sicher(fn) {
    try {
        return await fn();
    } catch (e) {
        toast(e.message, 'fehler');
        return undefined;
    }
}

// ---------- API ----------

async function api(method, path, body) {
    const start = performance.now();
    const options = { method, headers: {} };
    if (body !== undefined) {
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(body);
    }

    let res;
    try {
        res = await fetch(path, options);
    } catch {
        protokolliere(method, path, 0, start);
        throw new Error(`${method} ${path}: Backend nicht erreichbar`);
    }
    protokolliere(method, path, res.status, start);

    const text = await res.text();
    // Die Controller schicken Fehlermeldungen im Header "error" (teilweise mit Status 204).
    const fehler = res.headers.get('error');
    if (!res.ok || fehler) {
        let meldung = fehler;
        if (!meldung) {
            try {
                const json = JSON.parse(text);
                meldung = json.message || `${json.status} ${json.error}`;
            } catch {
                meldung = `${res.status} ${res.statusText}`;
            }
        }
        throw new Error(`${method} ${path}: ${meldung}`);
    }

    if (!text) return null;
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function protokolliere(method, path, status, start) {
    const endpoint = ENDPOINTS.find(e => e.method === method && e.regex.test(path));
    if (endpoint) {
        endpoint.aufrufe++;
        endpoint.letzterStatus = status;
    }
    state.log.unshift({ method, path, status, ms: Math.round(performance.now() - start), zeit: new Date() });
    state.log.length = Math.min(state.log.length, 100);
    if (state.view === 'api') renderApi();
}

const laden = {
    produkte: async () => { state.produkte = await api('GET', '/produkte') || []; },
    kategorien: async () => { state.kategorien = await api('GET', '/kategorie') || []; },
    kunden: async () => { state.kunden = await api('GET', '/kunden') || []; },
    kundenprofile: async () => { state.kundenprofile = await api('GET', '/kundenprofile') || []; },
};

async function ladeAlles() {
    await Promise.all(Object.values(laden).map(fn => sicher(fn)));
    renderAlles();
}

async function pruefeBackend() {
    const dot = $('#backendStatus');
    try {
        const antwort = await api('GET', '/test');
        dot.className = 'status-dot online';
        dot.title = `Backend online – /test antwortet „${antwort}“`;
    } catch {
        dot.className = 'status-dot offline';
        dot.title = 'Backend nicht erreichbar';
    }
}

// ---------- Navigation ----------

function zeigeView(view) {
    state.view = view;
    $$('#tabs button').forEach(b => b.classList.toggle('active', b.dataset.view === view));
    $$('.view').forEach(v => v.classList.toggle('active', v.id === `view-${view}`));
    if (view === 'bestellungen') renderBestellungen();
    if (view === 'api') renderApi();
}

function zeigeSub(sub) {
    $$('#subtabs button').forEach(b => b.classList.toggle('active', b.dataset.sub === sub));
    $$('.sub').forEach(s => s.classList.toggle('active', s.id === `sub-${sub}`));
}

// ---------- Rendering ----------

function renderAlles() {
    renderKundenSelect();
    renderFilter();
    renderShop();
    renderCart();
    renderVerwaltung();
    if (state.view === 'bestellungen') renderBestellungen();
    if (state.view === 'api') renderApi();
}

function renderKundenSelect() {
    if (!state.kunden.some(k => k.id === state.kundeId)) {
        state.kundeId = state.kunden.length ? state.kunden[0].id : null;
        speichereLokal('kundeId', state.kundeId);
    }
    $('#aktuellerKunde').innerHTML = state.kunden.length
        ? state.kunden.map(k => `<option value="${k.id}" ${k.id === state.kundeId ? 'selected' : ''}>${esc(kundeName(k))}</option>`).join('')
        : '<option value="">Kein Kunde</option>';
}

function renderFilter() {
    if (state.filterKategorie != null && !state.kategorien.some(k => k.id === state.filterKategorie)) {
        state.filterKategorie = null;
    }
    const chips = [{ id: null, name: 'Alle' }, ...state.kategorien];
    $('#kategorieFilter').innerHTML = chips.map(k => `
        <button class="chip ${k.id === state.filterKategorie ? 'active' : ''}" data-kategorie="${k.id ?? ''}"
            ${k.beschreibung ? `title="${esc(k.beschreibung)}"` : ''}>${esc(k.name)}</button>`).join('');
}

function renderShop() {
    const suche = state.suche.toLowerCase();
    const liste = state.produkte.filter(p =>
        (state.filterKategorie == null || (p.kategorien || []).some(k => k.id === state.filterKategorie)) &&
        (!suche || `${p.name} ${p.info}`.toLowerCase().includes(suche)));

    $('#produktAnzahl').textContent = `${liste.length} von ${state.produkte.length} Produkten`;
    $('#produktGrid').innerHTML = liste.length
        ? liste.map(p => `
            <article class="karte">
                <div class="karte-bild" style="--hue:${produktFarbe(p.id)}">${initiale(p.name)}</div>
                <div class="karte-inhalt">
                    <div class="tags">${(p.kategorien || []).map(k => `<span class="tag">${esc(k.name)}</span>`).join('')}</div>
                    <h3>${esc(p.name)}</h3>
                    <p class="muted">${esc(p.info)}</p>
                </div>
                <div class="karte-fuss">
                    <span class="preis">${euro.format(p.preis)}</span>
                    <button class="btn btn-primary" data-in-warenkorb="${p.id}">In den Warenkorb</button>
                </div>
            </article>`).join('')
        : leer('Keine Produkte gefunden', state.produkte.length
            ? 'Passe Suche oder Filter an.'
            : 'Lege unter Verwaltung → Produkte das erste Produkt an.');
}

// ---------- Warenkorb ----------

function cartPositionen() {
    return Object.entries(state.cart)
        .map(([id, menge]) => ({ produkt: state.produkte.find(p => p.id === Number(id)), menge }))
        .filter(pos => pos.produkt);
}

function aendereMenge(id, delta) {
    const menge = (state.cart[id] || 0) + delta;
    if (menge > 0) state.cart[id] = menge;
    else delete state.cart[id];
    speichereLokal('cart', state.cart);
    renderCart();
}

function renderCart() {
    const positionen = cartPositionen();
    $('#cartCount').textContent = positionen.reduce((summe, p) => summe + p.menge, 0);
    $('#cartItems').innerHTML = positionen.length
        ? positionen.map(({ produkt, menge }) => `
            <li class="cart-item">
                <div class="mini-bild" style="--hue:${produktFarbe(produkt.id)}">${initiale(produkt.name)}</div>
                <div class="cart-item-text">
                    <strong>${esc(produkt.name)}</strong>
                    <span class="muted">${euro.format(produkt.preis)}</span>
                </div>
                <div class="menge">
                    <button type="button" data-menge="${produkt.id}" data-delta="-1" aria-label="Weniger">−</button>
                    <span>${menge}</span>
                    <button type="button" data-menge="${produkt.id}" data-delta="1" aria-label="Mehr">+</button>
                </div>
            </li>`).join('')
        : '<li class="leer">Dein Warenkorb ist leer.</li>';

    const zwischensumme = positionen.reduce((summe, p) => summe + p.produkt.preis * p.menge, 0);
    const versand = Number($('#lieferart').value || 0);
    $('#summeZwischen').textContent = euro.format(zwischensumme);
    $('#summeVersand').textContent = euro.format(versand);
    $('#summeGesamt').textContent = euro.format(zwischensumme + versand);
    $('#bestellenBtn').disabled = !positionen.length;
}

function oeffneCart() {
    const kunde = aktuellerKunde();
    const profil = kunde && kunde.kundenprofil;
    const adresse = (profil && profil.adresse) || {};
    const form = $('#checkoutForm');
    $('#checkoutKunde').textContent = kunde ? kundeName(kunde) : 'keinen Kunden – bitte oben auswählen';
    form.elements.vorname.value = adresse.vorname ?? profil?.vorname ?? '';
    form.elements.name.value = adresse.name ?? profil?.name ?? '';
    form.elements.plz.value = adresse.plz || '';
    form.elements.ort.value = adresse.ort ?? '';
    form.elements.land.value = adresse.land || 'Deutschland';
    renderCart();
    $('#cart').classList.add('offen');
    $('#overlay').hidden = false;
}

function schliesseCart() {
    $('#cart').classList.remove('offen');
    $('#overlay').hidden = true;
}

function adresseAusForm(form) {
    const wert = name => form.elements[name].value.trim();
    return {
        name: wert('name'),
        vorname: wert('vorname'),
        ort: wert('ort'),
        plz: Number(wert('plz')) || 0,
        land: wert('land'),
    };
}

async function bestellen(event) {
    event.preventDefault();
    if (state.kundeId == null) {
        toast('Bitte oben zuerst einen Kunden auswählen.', 'fehler');
        return;
    }
    const form = event.target;
    const body = {
        kundeId: state.kundeId,
        // Pro Stück wird die Produkt-ID einmal übergeben – das Backend summiert die Preise.
        produktIds: cartPositionen().flatMap(({ produkt, menge }) => Array(menge).fill(produkt.id)),
        lieferungsKosten: Number(form.elements.lieferart.value),
        adresse: adresseAusForm(form),
    };

    const bestellung = await sicher(() => api('POST', '/bestellung/bestellen', body));
    if (!bestellung) return;

    state.cart = {};
    speichereLokal('cart', state.cart);
    schliesseCart();
    toast(`Bestellung #${bestellung.id} aufgegeben – ${euro.format(bestellung.gesamtKosten)}`);
    await sicher(laden.kunden);
    renderAlles();
    zeigeView('bestellungen');
}

// ---------- Bestellungen ----------

async function renderBestellungen() {
    const box = $('#bestellungenListe');
    const kunde = aktuellerKunde();
    $('#bestellungenKunde').textContent = kunde ? `für ${kundeName(kunde)}` : '';
    if (!kunde) {
        box.innerHTML = leer('Kein Kunde ausgewählt', 'Lege unter Verwaltung → Kunden einen Kunden an.');
        return;
    }

    box.innerHTML = '<p class="muted">Lade Bestellungen…</p>';
    const liste = await sicher(() => api('GET', `/kunde/bestellungen/${kunde.id}`));
    if (!liste) {
        box.innerHTML = leer('Bestellungen konnten nicht geladen werden');
        return;
    }
    box.innerHTML = liste.length
        ? [...liste].sort((a, b) => b.id - a.id).map(bestellungHtml).join('')
        : leer('Noch keine Bestellungen', 'Lege Produkte in den Warenkorb und bestelle.');
}

function bestellungHtml(b) {
    const gruppiert = new Map();
    (b.produkte || []).forEach(p => {
        const g = gruppiert.get(p.id) || { produkt: p, menge: 0 };
        g.menge++;
        gruppiert.set(p.id, g);
    });
    const a = b.adresse;

    return `
        <article class="bestellung">
            <header>
                <div><strong>Bestellung #${b.id}</strong><span class="muted"> · ${datum(b.datum)}</span></div>
                <span class="badge status-${esc(b.status)}">${esc(STATUS[b.status] || b.status || 'ohne Status')}</span>
            </header>
            <ul class="positionen">
                ${[...gruppiert.values()].map(({ produkt, menge }) => `
                    <li><span>${menge}× ${esc(produkt.name)}</span><span>${euro.format(produkt.preis * menge)}</span></li>`).join('')}
                <li class="muted"><span>Versand</span><span>${euro.format(b.lieferKosten)}</span></li>
                <li class="summe"><span>Gesamt</span><span>${euro.format(b.gesamtKosten)}</span></li>
            </ul>
            <footer>
                <span class="muted">${a
                    ? `Lieferung an ${esc(a.vorname)} ${esc(a.name)}, ${esc(adresseText(a))}`
                    : 'Keine Lieferadresse'}</span>
                <form class="status-form" data-bestellung="${b.id}">
                    <select name="status" aria-label="Status">
                        ${Object.entries(STATUS).map(([wert, label]) =>
                            `<option value="${wert}" ${wert === b.status ? 'selected' : ''}>${label}</option>`).join('')}
                    </select>
                    <button class="btn btn-klein">Status setzen</button>
                </form>
            </footer>
        </article>`;
}

async function aendereStatus(event) {
    event.preventDefault();
    const form = event.target;
    const id = form.dataset.bestellung;
    const status = form.elements.status.value;
    // Der Controller erwartet das Enum direkt als JSON-String, z. B. "BESTELLT".
    const ok = await sicher(() => api('PUT', `/bestellung/status/${id}`, status));
    if (ok === undefined) return;
    toast(`Bestellung #${id}: ${STATUS[status]}`);
    renderBestellungen();
}

// ---------- Verwaltung ----------

function kategorieChecks(ausgewaehlt = new Set()) {
    return state.kategorien.length
        ? state.kategorien.map(k => `
            <label class="check">
                <input type="checkbox" name="kategorie" value="${k.id}" ${ausgewaehlt.has(k.id) ? 'checked' : ''}>
                ${esc(k.name)}
            </label>`).join('')
        : '<span class="muted klein">Noch keine Kategorien angelegt.</span>';
}

function renderVerwaltung() {
    const ausgewaehlt = new Set($$('#neuProduktKategorien input:checked').map(i => Number(i.value)));
    $('#neuProduktKategorien').innerHTML = kategorieChecks(ausgewaehlt);

    $('#produktTabelle').innerHTML = state.produkte.length
        ? state.produkte.map(p => `
            <tr>
                <td class="muted">#${p.id}</td>
                <td><strong>${esc(p.name)}</strong><div class="muted klein">${esc(p.info)}</div></td>
                <td class="zahl">${euro.format(p.preis)}</td>
                <td>${(p.kategorien || []).map(k => `<span class="tag">${esc(k.name)}</span>`).join(' ') || '<span class="muted">–</span>'}</td>
                <td class="aktionen">
                    <button class="btn btn-klein" data-produkt-bearbeiten="${p.id}">Bearbeiten</button>
                    <button class="btn btn-klein btn-gefahr" data-produkt-loeschen="${p.id}">Löschen</button>
                </td>
            </tr>`).join('')
        : `<tr><td colspan="5">${leer('Noch keine Produkte', 'Lege oben ein Produkt an.')}</td></tr>`;

    $('#kategorieTabelle').innerHTML = state.kategorien.length
        ? state.kategorien.map(k => {
            const anzahl = state.produkte.filter(p => (p.kategorien || []).some(pk => pk.id === k.id)).length;
            return `
                <tr>
                    <td class="muted">#${k.id}</td>
                    <td><strong>${esc(k.name)}</strong></td>
                    <td>${esc(k.beschreibung) || '<span class="muted">–</span>'}</td>
                    <td class="zahl">${anzahl}</td>
                    <td class="aktionen">
                        <button class="btn btn-klein btn-gefahr" data-kategorie-loeschen="${k.id}">Löschen</button>
                    </td>
                </tr>`;
        }).join('')
        : `<tr><td colspan="5">${leer('Noch keine Kategorien', 'Lege oben eine Kategorie an.')}</td></tr>`;

    $('#kundeTabelle').innerHTML = state.kunden.length
        ? state.kunden.map(k => `
            <tr>
                <td class="muted">#${k.id}</td>
                <td><strong>${esc(kundeName(k))}</strong>${k.id === state.kundeId ? ' <span class="tag">aktiv</span>' : ''}</td>
                <td>${datum(k.kundenprofil?.geb)}</td>
                <td>${esc(adresseText(k.kundenprofil?.adresse))}</td>
                <td class="zahl">${(k.bestellungen || []).length}</td>
                <td class="aktionen">
                    <button class="btn btn-klein" data-kunde-waehlen="${k.id}">Auswählen</button>
                    <button class="btn btn-klein btn-gefahr" data-kunde-loeschen="${k.id}">Löschen</button>
                </td>
            </tr>`).join('')
        : `<tr><td colspan="6">${leer('Noch keine Kunden', 'Lege oben einen Kunden an.')}</td></tr>`;

    const profileMitKunde = new Set(state.kunden.map(k => k.kundenprofil?.id).filter(id => id != null));
    $('#kundenprofilTabelle').innerHTML = state.kundenprofile.length
        ? state.kundenprofile.map(p => `
            <tr>
                <td class="muted">#${p.id}</td>
                <td><strong>${esc(`${p.vorname ?? ''} ${p.name ?? ''}`.trim())}</strong></td>
                <td>${datum(p.geb)}</td>
                <td>${esc(adresseText(p.adresse))}</td>
                <td>${profileMitKunde.has(p.id) ? 'ja' : '<span class="muted">nein</span>'}</td>
            </tr>`).join('')
        : `<tr><td colspan="5">${leer('Noch keine Kundenprofile')}</td></tr>`;
}

function produktAusForm(form) {
    return {
        name: form.elements.name.value.trim(),
        // ProduktDAO.preis ist im Backend ein int.
        preis: Math.round(Number(form.elements.preis.value)),
        info: form.elements.info.value.trim(),
    };
}

function profilAusForm(form) {
    return {
        name: form.elements.name.value.trim(),
        vorname: form.elements.vorname.value.trim(),
        geb: form.elements.geb.value || null,
        adresse: adresseAusForm(form),
    };
}

async function legeProduktAn(event) {
    event.preventDefault();
    const form = event.target;
    const kategorieIds = $$('input[name=kategorie]:checked', form).map(i => Number(i.value));
    const produkt = await sicher(async () => {
        const neu = await api('POST', '/produkte', produktAusForm(form));
        return kategorieIds.length ? api('PUT', `/produkt/${neu.id}/addkategorie`, kategorieIds) : neu;
    });
    if (!produkt) return;
    form.reset();
    $$('input[name=kategorie]', form).forEach(i => { i.checked = false; });
    toast(`Produkt „${produkt.name}“ angelegt`);
    await sicher(laden.produkte);
    renderAlles();
}

function oeffneProduktDialog(id) {
    const produkt = state.produkte.find(p => p.id === id);
    if (!produkt) return;
    const form = $('#produktDialogForm');
    form.dataset.id = id;
    form.elements.name.value = produkt.name ?? '';
    form.elements.preis.value = produkt.preis ?? 0;
    form.elements.info.value = produkt.info ?? '';
    $('#dialogTitel').textContent = `Produkt #${id} bearbeiten`;
    $('#dialogKategorien').innerHTML = kategorieChecks(new Set((produkt.kategorien || []).map(k => k.id)));
    $('#produktDialog').showModal();
}

async function speichereProdukt(event) {
    event.preventDefault();
    const form = event.target;
    const id = Number(form.dataset.id);
    const produkt = state.produkte.find(p => p.id === id);
    const vorher = new Set((produkt?.kategorien || []).map(k => k.id));
    const nachher = new Set($$('input[name=kategorie]:checked', form).map(i => Number(i.value)));
    const hinzu = [...nachher].filter(k => !vorher.has(k));
    const weg = [...vorher].filter(k => !nachher.has(k));

    const ok = await sicher(async () => {
        await api('PUT', `/produkte/${id}`, produktAusForm(form));
        if (hinzu.length) await api('PUT', `/produkt/${id}/addkategorie`, hinzu);
        if (weg.length) await api('PUT', `/produkt/${id}/removekategorie`, weg);
        return true;
    });
    if (!ok) return;
    $('#produktDialog').close();
    toast('Produkt gespeichert');
    await sicher(laden.produkte);
    renderAlles();
}

async function legeKategorieAn(event) {
    event.preventDefault();
    const form = event.target;
    const kategorie = await sicher(() => api('POST', '/kategorie', {
        name: form.elements.name.value.trim(),
        beschreibung: form.elements.beschreibung.value.trim(),
    }));
    if (!kategorie) return;
    form.reset();
    toast(`Kategorie „${kategorie.name}“ angelegt`);
    await sicher(laden.kategorien);
    renderAlles();
}

async function legeKundeAn(event) {
    event.preventDefault();
    const form = event.target;
    const kunde = await sicher(() => api('POST', '/kunden', { kundenprofil: profilAusForm(form) }));
    if (!kunde) return;
    form.reset();
    toast(`Kunde ${kundeName(kunde)} angelegt`);
    state.kundeId = kunde.id;
    speichereLokal('kundeId', state.kundeId);
    await Promise.all([sicher(laden.kunden), sicher(laden.kundenprofile)]);
    renderAlles();
}

async function legeKundenprofilAn(event) {
    event.preventDefault();
    const form = event.target;
    const profil = await sicher(() => api('POST', '/kundenprofile', profilAusForm(form)));
    if (!profil) return;
    form.reset();
    toast(`Kundenprofil #${profil.id} angelegt`);
    await sicher(laden.kundenprofile);
    renderAlles();
}

async function loesche(frage, path, neuLaden) {
    if (!confirm(frage)) return;
    const ok = await sicher(async () => {
        await api('DELETE', path);
        return true;
    });
    if (!ok) return;
    toast('Gelöscht');
    await Promise.all(neuLaden.map(fn => sicher(fn)));
    renderAlles();
}

function waehleKunde(id) {
    state.kundeId = id;
    speichereLokal('kundeId', id);
    renderAlles();
    toast(`${kundeName(aktuellerKunde())} ist jetzt der aktive Kunde`);
}

// ---------- API-Ansicht ----------

function statusCode(status) {
    const art = status >= 200 && status < 300 ? 'ok' : 'fehler';
    return `<span class="code code-${art}">${status || 'offline'}</span>`;
}

function renderApi() {
    const aufgerufen = ENDPOINTS.filter(e => e.aufrufe > 0).length;
    $('#apiAbdeckung').textContent = `${aufgerufen} von ${ENDPOINTS.length} Endpunkten in dieser Sitzung aufgerufen`;
    $('#apiFortschritt').style.width = `${(aufgerufen / ENDPOINTS.length) * 100}%`;

    $('#endpointTabelle').innerHTML = ENDPOINTS.map(e => `
        <tr class="${e.aufrufe ? '' : 'ungenutzt'}">
            <td><span class="methode methode-${e.method}">${e.method}</span></td>
            <td><code>${esc(e.path)}</code></td>
            <td>${esc(e.zweck)}</td>
            <td class="zahl">${e.aufrufe}</td>
            <td>${e.letzterStatus == null ? '<span class="muted">–</span>' : statusCode(e.letzterStatus)}</td>
        </tr>`).join('');

    $('#apiLog').innerHTML = state.log.length
        ? state.log.map(l => `
            <li>
                <span class="muted">${l.zeit.toLocaleTimeString('de-DE')}</span>
                <span class="methode methode-${l.method}">${l.method}</span>
                <code>${esc(l.path)}</code>
                ${statusCode(l.status)}
                <span class="muted">${l.ms} ms</span>
            </li>`).join('')
        : '<li class="muted">Noch keine Aufrufe.</li>';
}

// ---------- Events ----------

function bindeEvents() {
    document.addEventListener('click', event => {
        const button = event.target.closest('button');
        if (!button) return;
        const d = button.dataset;

        if (d.view) zeigeView(d.view);
        else if (d.sub) zeigeSub(d.sub);
        else if ('kategorie' in d) {
            state.filterKategorie = d.kategorie === '' ? null : Number(d.kategorie);
            renderFilter();
            renderShop();
        } else if (d.inWarenkorb) {
            aendereMenge(d.inWarenkorb, 1);
            const produkt = state.produkte.find(p => p.id === Number(d.inWarenkorb));
            toast(`${produkt ? produkt.name : 'Produkt'} liegt im Warenkorb`);
        } else if (d.menge) aendereMenge(d.menge, Number(d.delta));
        else if (d.produktBearbeiten) oeffneProduktDialog(Number(d.produktBearbeiten));
        else if (d.produktLoeschen) {
            loesche(`Produkt #${d.produktLoeschen} wirklich löschen?`, `/produkte/${d.produktLoeschen}`,
                [laden.produkte, laden.kunden]);
        } else if (d.kategorieLoeschen) {
            loesche(`Kategorie #${d.kategorieLoeschen} wirklich löschen?`, `/kategorie/${d.kategorieLoeschen}`,
                [laden.kategorien, laden.produkte]);
        } else if (d.kundeLoeschen) {
            loesche(`Kunde #${d.kundeLoeschen} wirklich löschen?`, `/kunden/${d.kundeLoeschen}`,
                [laden.kunden, laden.kundenprofile]);
        } else if (d.kundeWaehlen) waehleKunde(Number(d.kundeWaehlen));
        else if ('dialogSchliessen' in d) button.closest('dialog').close();
    });

    $('#cartBtn').addEventListener('click', oeffneCart);
    $('#cartSchliessen').addEventListener('click', schliesseCart);
    $('#overlay').addEventListener('click', schliesseCart);
    document.addEventListener('keydown', e => { if (e.key === 'Escape') schliesseCart(); });

    $('#suche').addEventListener('input', e => {
        state.suche = e.target.value;
        renderShop();
    });

    $('#aktuellerKunde').addEventListener('change', e => {
        state.kundeId = e.target.value ? Number(e.target.value) : null;
        speichereLokal('kundeId', state.kundeId);
        renderVerwaltung();
        if (state.view === 'bestellungen') renderBestellungen();
    });

    $('#lieferart').innerHTML = LIEFERARTEN
        .map(l => `<option value="${l.kosten}">${l.label} (${euro.format(l.kosten)})</option>`).join('');
    $('#lieferart').addEventListener('change', renderCart);

    $('#checkoutForm').addEventListener('submit', bestellen);
    $('#bestellungenListe').addEventListener('submit', aendereStatus);
    $('#produktForm').addEventListener('submit', legeProduktAn);
    $('#produktDialogForm').addEventListener('submit', speichereProdukt);
    $('#kategorieForm').addEventListener('submit', legeKategorieAn);
    $('#kundeForm').addEventListener('submit', legeKundeAn);
    $('#kundenprofilForm').addEventListener('submit', legeKundenprofilAn);
    $('#neuLadenBtn').addEventListener('click', () => { pruefeBackend(); ladeAlles(); });
}

// ---------- Start ----------

state.cart = ladeLokal('cart', {});
state.kundeId = ladeLokal('kundeId', null);
bindeEvents();
pruefeBackend();
ladeAlles();
