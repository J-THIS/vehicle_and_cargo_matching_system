package com.example.vehicle_and_cargo_matching_system.util;

import java.io.Serializable;
import java.util.List;

public class AddressUtil implements Serializable {
    private String code;
    private String name;
    private List<Child> cityList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getCityList() {
        return cityList;
    }

    public void setCityList(List<Child> cityList) {
        this.cityList = cityList;
    }

    public static class Child implements Serializable {
        private String code;
        private String name;
        private List<Grandchild> areaList;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Grandchild> getAreaList() {
            return areaList;
        }

        public void setAreaList(List<Grandchild> areaList) {
            this.areaList = areaList;
        }

        public static class Grandchild implements Serializable {
            private String code;
            private String name;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

