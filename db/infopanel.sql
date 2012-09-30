--
-- PostgreSQL database dump
--

-- Started on 2010-11-13 03:06:42

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 340 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1545 (class 1259 OID 17086)
-- Dependencies: 3
-- Name: dev_trouble; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE dev_trouble (
    _trouble integer NOT NULL,
    _devcapsule integer NOT NULL
);


ALTER TABLE public.dev_trouble OWNER TO root;

--
-- TOC entry 1542 (class 1259 OID 16619)
-- Dependencies: 3
-- Name: devcapsule; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE devcapsule (
    id integer NOT NULL,
    timedown text,
    timeup text,
    _device integer,
    complete boolean
);


ALTER TABLE public.devcapsule OWNER TO root;

--
-- TOC entry 1541 (class 1259 OID 16617)
-- Dependencies: 3 1542
-- Name: devcapsule_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE devcapsule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.devcapsule_id_seq OWNER TO root;

--
-- TOC entry 1867 (class 0 OID 0)
-- Dependencies: 1541
-- Name: devcapsule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE devcapsule_id_seq OWNED BY devcapsule.id;


--
-- TOC entry 1538 (class 1259 OID 16561)
-- Dependencies: 3
-- Name: device; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE device (
    id integer NOT NULL,
    name text,
    description text,
    _status integer,
    _group integer
);


ALTER TABLE public.device OWNER TO postgres;

--
-- TOC entry 1537 (class 1259 OID 16559)
-- Dependencies: 1538 3
-- Name: device_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.device_id_seq OWNER TO postgres;

--
-- TOC entry 1868 (class 0 OID 0)
-- Dependencies: 1537
-- Name: device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE device_id_seq OWNED BY device.id;


--
-- TOC entry 1528 (class 1259 OID 16499)
-- Dependencies: 3
-- Name: hostgroup; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE hostgroup (
    id integer NOT NULL,
    num integer,
    name text
);


ALTER TABLE public.hostgroup OWNER TO root;

--
-- TOC entry 1527 (class 1259 OID 16497)
-- Dependencies: 1528 3
-- Name: hostgroup_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE hostgroup_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.hostgroup_id_seq OWNER TO root;

--
-- TOC entry 1869 (class 0 OID 0)
-- Dependencies: 1527
-- Name: hostgroup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE hostgroup_id_seq OWNED BY hostgroup.id;


--
-- TOC entry 1530 (class 1259 OID 16510)
-- Dependencies: 3
-- Name: hoststatus; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE hoststatus (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.hoststatus OWNER TO root;

--
-- TOC entry 1529 (class 1259 OID 16508)
-- Dependencies: 1530 3
-- Name: hoststatus_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE hoststatus_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.hoststatus_id_seq OWNER TO root;

--
-- TOC entry 1870 (class 0 OID 0)
-- Dependencies: 1529
-- Name: hoststatus_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE hoststatus_id_seq OWNED BY hoststatus.id;


--
-- TOC entry 1532 (class 1259 OID 16521)
-- Dependencies: 3
-- Name: service; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE service (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.service OWNER TO root;

--
-- TOC entry 1531 (class 1259 OID 16519)
-- Dependencies: 1532 3
-- Name: service_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE service_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.service_id_seq OWNER TO root;

--
-- TOC entry 1871 (class 0 OID 0)
-- Dependencies: 1531
-- Name: service_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE service_id_seq OWNED BY service.id;


--
-- TOC entry 1533 (class 1259 OID 16530)
-- Dependencies: 3
-- Name: tl_t; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE tl_t (
    _trouble integer NOT NULL,
    _troublelist integer NOT NULL
);


ALTER TABLE public.tl_t OWNER TO root;

--
-- TOC entry 1540 (class 1259 OID 16582)
-- Dependencies: 3
-- Name: trouble; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE trouble (
    title text,
    legend text,
    timeout text,
    date_in text,
    date_out text,
    description text,
    id integer NOT NULL,
    _status integer,
    close boolean,
    crm boolean
);


ALTER TABLE public.trouble OWNER TO postgres;

--
-- TOC entry 1539 (class 1259 OID 16580)
-- Dependencies: 1540 3
-- Name: trouble_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trouble_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.trouble_id_seq OWNER TO postgres;

--
-- TOC entry 1872 (class 0 OID 0)
-- Dependencies: 1539
-- Name: trouble_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trouble_id_seq OWNED BY trouble.id;


--
-- TOC entry 1534 (class 1259 OID 16535)
-- Dependencies: 3
-- Name: trouble_service; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE trouble_service (
    _trouble integer NOT NULL,
    _service integer NOT NULL
);


ALTER TABLE public.trouble_service OWNER TO root;

--
-- TOC entry 1536 (class 1259 OID 16542)
-- Dependencies: 3
-- Name: troublelist; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE troublelist (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.troublelist OWNER TO root;

--
-- TOC entry 1535 (class 1259 OID 16540)
-- Dependencies: 3 1536
-- Name: troublelist_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE troublelist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.troublelist_id_seq OWNER TO root;

--
-- TOC entry 1873 (class 0 OID 0)
-- Dependencies: 1535
-- Name: troublelist_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE troublelist_id_seq OWNED BY troublelist.id;


--
-- TOC entry 1544 (class 1259 OID 16635)
-- Dependencies: 3
-- Name: troublestatus; Type: TABLE; Schema: public; Owner: root; Tablespace: 
--

CREATE TABLE troublestatus (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.troublestatus OWNER TO root;

--
-- TOC entry 1543 (class 1259 OID 16633)
-- Dependencies: 3 1544
-- Name: troublestatus_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE troublestatus_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.troublestatus_id_seq OWNER TO root;

--
-- TOC entry 1874 (class 0 OID 0)
-- Dependencies: 1543
-- Name: troublestatus_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE troublestatus_id_seq OWNED BY troublestatus.id;


--
-- TOC entry 1829 (class 2604 OID 16622)
-- Dependencies: 1542 1541 1542
-- Name: id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE devcapsule ALTER COLUMN id SET DEFAULT nextval('devcapsule_id_seq'::regclass);


--
-- TOC entry 1827 (class 2604 OID 16564)
-- Dependencies: 1538 1537 1538
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE device ALTER COLUMN id SET DEFAULT nextval('device_id_seq'::regclass);


--
-- TOC entry 1823 (class 2604 OID 16502)
-- Dependencies: 1528 1527 1528
-- Name: id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE hostgroup ALTER COLUMN id SET DEFAULT nextval('hostgroup_id_seq'::regclass);


--
-- TOC entry 1824 (class 2604 OID 16513)
-- Dependencies: 1529 1530 1530
-- Name: id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE hoststatus ALTER COLUMN id SET DEFAULT nextval('hoststatus_id_seq'::regclass);


--
-- TOC entry 1825 (class 2604 OID 16524)
-- Dependencies: 1532 1531 1532
-- Name: id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE service ALTER COLUMN id SET DEFAULT nextval('service_id_seq'::regclass);


--
-- TOC entry 1828 (class 2604 OID 16585)
-- Dependencies: 1540 1539 1540
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE trouble ALTER COLUMN id SET DEFAULT nextval('trouble_id_seq'::regclass);


--
-- TOC entry 1826 (class 2604 OID 16545)
-- Dependencies: 1535 1536 1536
-- Name: id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE troublelist ALTER COLUMN id SET DEFAULT nextval('troublelist_id_seq'::regclass);


--
-- TOC entry 1830 (class 2604 OID 16638)
-- Dependencies: 1543 1544 1544
-- Name: id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE troublestatus ALTER COLUMN id SET DEFAULT nextval('troublestatus_id_seq'::regclass);


--
-- TOC entry 1852 (class 2606 OID 17090)
-- Dependencies: 1545 1545 1545
-- Name: dev_trouble_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY dev_trouble
    ADD CONSTRAINT dev_trouble_pkey PRIMARY KEY (_trouble, _devcapsule);


--
-- TOC entry 1848 (class 2606 OID 16627)
-- Dependencies: 1542 1542
-- Name: devcapsule_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY devcapsule
    ADD CONSTRAINT devcapsule_pkey PRIMARY KEY (id);


--
-- TOC entry 1844 (class 2606 OID 16569)
-- Dependencies: 1538 1538
-- Name: device_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device_pkey PRIMARY KEY (id);


--
-- TOC entry 1832 (class 2606 OID 16507)
-- Dependencies: 1528 1528
-- Name: hostgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY hostgroup
    ADD CONSTRAINT hostgroup_pkey PRIMARY KEY (id);


--
-- TOC entry 1834 (class 2606 OID 16518)
-- Dependencies: 1530 1530
-- Name: hoststatus_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY hoststatus
    ADD CONSTRAINT hoststatus_pkey PRIMARY KEY (id);


--
-- TOC entry 1836 (class 2606 OID 16529)
-- Dependencies: 1532 1532
-- Name: service_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY service
    ADD CONSTRAINT service_pkey PRIMARY KEY (id);


--
-- TOC entry 1838 (class 2606 OID 16534)
-- Dependencies: 1533 1533 1533
-- Name: tl_t_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY tl_t
    ADD CONSTRAINT tl_t_pkey PRIMARY KEY (_trouble, _troublelist);


--
-- TOC entry 1846 (class 2606 OID 16590)
-- Dependencies: 1540 1540
-- Name: trouble_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY trouble
    ADD CONSTRAINT trouble_pkey PRIMARY KEY (id);


--
-- TOC entry 1840 (class 2606 OID 16539)
-- Dependencies: 1534 1534 1534
-- Name: trouble_service_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY trouble_service
    ADD CONSTRAINT trouble_service_pkey PRIMARY KEY (_trouble, _service);


--
-- TOC entry 1842 (class 2606 OID 16550)
-- Dependencies: 1536 1536
-- Name: troublelist_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY troublelist
    ADD CONSTRAINT troublelist_pkey PRIMARY KEY (id);


--
-- TOC entry 1850 (class 2606 OID 16643)
-- Dependencies: 1544 1544
-- Name: troublestatus_pkey; Type: CONSTRAINT; Schema: public; Owner: root; Tablespace: 
--

ALTER TABLE ONLY troublestatus
    ADD CONSTRAINT troublestatus_pkey PRIMARY KEY (id);


--
-- TOC entry 1860 (class 2606 OID 17091)
-- Dependencies: 1545 1847 1542
-- Name: dev_trouble__devcapsule_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY dev_trouble
    ADD CONSTRAINT dev_trouble__devcapsule_fkey FOREIGN KEY (_devcapsule) REFERENCES devcapsule(id);


--
-- TOC entry 1861 (class 2606 OID 17096)
-- Dependencies: 1540 1545 1845
-- Name: dev_trouble__trouble_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY dev_trouble
    ADD CONSTRAINT dev_trouble__trouble_fkey FOREIGN KEY (_trouble) REFERENCES trouble(id);


--
-- TOC entry 1859 (class 2606 OID 16628)
-- Dependencies: 1538 1542 1843
-- Name: devcapsule__device_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY devcapsule
    ADD CONSTRAINT devcapsule__device_fkey FOREIGN KEY (_device) REFERENCES device(id);


--
-- TOC entry 1858 (class 2606 OID 16575)
-- Dependencies: 1831 1538 1528
-- Name: device__group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device__group_fkey FOREIGN KEY (_group) REFERENCES hostgroup(id);


--
-- TOC entry 1857 (class 2606 OID 16570)
-- Dependencies: 1833 1538 1530
-- Name: device__status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device__status_fkey FOREIGN KEY (_status) REFERENCES hoststatus(id);


--
-- TOC entry 1853 (class 2606 OID 16597)
-- Dependencies: 1533 1540 1845
-- Name: tl_t__trouble_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY tl_t
    ADD CONSTRAINT tl_t__trouble_fkey FOREIGN KEY (_trouble) REFERENCES trouble(id);


--
-- TOC entry 1854 (class 2606 OID 16602)
-- Dependencies: 1841 1536 1533
-- Name: tl_t__troublelist_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY tl_t
    ADD CONSTRAINT tl_t__troublelist_fkey FOREIGN KEY (_troublelist) REFERENCES troublelist(id);


--
-- TOC entry 1856 (class 2606 OID 16612)
-- Dependencies: 1835 1534 1532
-- Name: trouble_service__service_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY trouble_service
    ADD CONSTRAINT trouble_service__service_fkey FOREIGN KEY (_service) REFERENCES service(id);


--
-- TOC entry 1855 (class 2606 OID 16607)
-- Dependencies: 1534 1845 1540
-- Name: trouble_service__trouble_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY trouble_service
    ADD CONSTRAINT trouble_service__trouble_fkey FOREIGN KEY (_trouble) REFERENCES trouble(id);


--
-- TOC entry 1866 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-11-13 03:06:44

--
-- PostgreSQL database dump complete
--

