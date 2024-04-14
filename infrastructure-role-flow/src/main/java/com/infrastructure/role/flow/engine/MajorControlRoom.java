package com.infrastructure.role.flow.engine;


import cn.hutool.core.collection.ListUtil;
import com.infrastructure.role.flow.domain.RequestFacade;
import com.infrastructure.role.flow.domain.ResponseFacade;
import com.infrastructure.role.flow.valves.Valve;
import com.infrastructure.role.flow.valves.ValveSensor;

import java.util.ArrayList;
import java.util.List;


public class MajorControlRoom implements Engine {




    private final List<Valve> valveList = new ArrayList<>();




    @Override
    public void authorize(RequestFacade request, ResponseFacade response){
        new StandardValveSensor().invokeNext(request,response);
    }

    @Override
    public void addValve(Valve valve) {
        valveList.add(valve);
    }

    @Override
    public void addValveList(List<Valve> valveList) {
        this.valveList.addAll(valveList);
    }

    @Override
    public List<Valve> getValveList() {
        return ListUtil.toCopyOnWriteArrayList(valveList);
    }

    protected class StandardValveSensor implements ValveSensor {

        public StandardValveSensor(){
        }

        protected int now = 0;

        private int next = 1;

        @Override
        public void invokeNext(RequestFacade request, ResponseFacade response) {
            int subscript = now;
            now = now + next;

            // Invoke the requested Valve for the current request thread
            if (subscript < valveList.size()) {
                valveList.get(subscript).invoke(request, response, this);
            }
        }
    }


    /**
     *  引导阀
     * @Date 2024/4/14 16:49
    **/
    private static class PilotValve{

    }


}
