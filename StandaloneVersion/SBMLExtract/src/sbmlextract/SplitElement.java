/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sbmlextract;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
/**
 *
 * @author Mathialakan.Thavappi
 */
public class SplitElement {
   
    SBMLDocument originalDoc, gripedDoc ;
    SBMLDocument[] splitedDocs ;
    Model[] splitedModels ;
    Model originalModel, gripedModel;
    
    public SplitElement() {
        this.originalDoc = new SBMLDocument();  
    }
    
    public SplitElement( SBMLDocument originalDoc) {
        this.originalDoc = originalDoc;  
    }
 
    public SplitElement(Model originalModel ) {
        this.originalModel = originalModel; 
    }
    
    public Model  getModel() {
        return originalModel;
    }
    
    public void  setModel(Model originalModel) {
        this.originalModel = originalModel;
    }
    
    public Model  getGripedModel() {
        return gripedModel;
    }
    
    public void  setGripedModels(Model[] splitedModels) {
        this.splitedModels = splitedModels;
        System.out.println("splitedModels: "+splitedModels.length);
         System.out.println("splitedModels: getId "+splitedModels[0].getCompartment(0).getId());
    }
    
    public void  setGripedModel(Model gripedModel) {
        this.gripedModel = gripedModel;
    }
    
    public Model[]  getModels() {
        return splitedModels;
    }
    
    public SBMLDocument getDocument() {  
        return originalDoc;
    }
    
    public void setDocument(SBMLDocument originalDoc ) {
        this.originalDoc = originalDoc;
    } 
   
    public SBMLDocument getGripedDocument() {  
        return gripedDoc;
    }
    
    public void setGripedDocument(SBMLDocument gripedDoc) {  
        this.gripedDoc = gripedDoc;
    }
     
    public void setGripedDocuments(SBMLDocument[] splitedDocs) {  
        this.splitedDocs = splitedDocs;
    }
    
    public void setGripedDocuments() {
        
        SBMLDocument[] temp= new SBMLDocument[splitedModels.length];
        for(int i=0; i< splitedModels.length; i++){ 
            temp[i]= new SBMLDocument(originalDoc.getLevel(), originalDoc.getVersion());      
            temp[i].setModel(splitedModels[i]); 
        }
        this.splitedDocs =temp;
        
    }
    
    public void setGripedDocuments(Model[] splitedModels) { 
       SBMLDocument[] temp = new SBMLDocument[splitedModels.length];
        for(int i=0; i< splitedModels.length; i++){
            temp[i]= new SBMLDocument(originalDoc.getLevel(), originalDoc.getVersion()); 
            temp[i].setModel(splitedModels[i]);
        }
            
        this.splitedDocs =temp;    
    }
    
    public SBMLDocument[]  getDocuments() {  
        return splitedDocs;
    }
    
    public SBMLDocument gripDocument(String id, String how){     
        SBMLDocument temp = new SBMLDocument(originalDoc.getLevel(), originalDoc.getVersion());
        setModel(originalDoc.getModel());
        switch(how){
            case "C" : setGripedModel(gripCompartment(id)); break;
            case "R" : setGripedModel(gripReaction(id)); break;
        }       
        temp.setModel(gripedModel);
        return temp;
    }
    
    public SBMLDocument[] gripDocuments(String how){     
        setModel(originalDoc.getModel());
        switch(how){
            case "C" : setGripedModels(gripCompartmentList()); break;
            case "R" : setGripedModels(gripReactionList()); break;
        }       
        setGripedDocuments();
        return getDocuments();
    }
    
    public boolean splitByCompartment(){
        int size = originalModel.getNumCompartments(); 
        Model[] models = new Model[size];
        for(int i=0; i< size; i++){
           org.sbml.jsbml.Compartment compartment = originalModel.getListOfCompartments().get(i); 
           models[i] = gripCompartment(compartment);
        }
        return true;
    }
    
    public boolean splitByReaction(){
        int size = originalModel.getNumReactions(); 
        Model[] models = new Model[size];
        for(int i=0; i< size; i++){
           org.sbml.jsbml.Reaction react = originalModel.getListOfReactions().get(i); 
           models[i] = gripReaction(react);   
        }      
        return true;
    }
    
     public org.sbml.jsbml.Model gripCompartment( org.sbml.jsbml.Compartment compartment){
        Model newModel = new Model(compartment.getLevel(), compartment.getVersion());
        insertCompartment(newModel, compartment );
        for(org.sbml.jsbml.Species species: originalModel.getListOfSpecies())
            if(species.getCompartment().equals(compartment.getId())) insertSpecies(newModel, species);
        for(org.sbml.jsbml.Reaction reaction: originalModel.getListOfReactions())      
            if(compartmentOfReaction(reaction).equals(compartment.getId())) insertReaction(newModel,reaction ); 
         System.out.println("id: "+compartment.getId());            
        return newModel; 
        
    }
    
    public org.sbml.jsbml.Model gripCompartment(String id){
        org.sbml.jsbml.Compartment compartment =  originalModel.getCompartment(id);
        System.out.println("id: "+id);
        return gripCompartment(compartment); 
        
    }
    
    public org.sbml.jsbml.Model[] gripCompartmentList( ){
        int nComp = originalModel.getNumCompartments();
        Model[] newModels = new org.sbml.jsbml.Model[nComp];
        int i=0;
        for(org.sbml.jsbml.Compartment compartment: originalModel.getListOfCompartments()){
             newModels[i++] = gripCompartment(compartment);  
        }
       System.out.println("i: "+i);
       return newModels; 
    }
    
    private String compartmentOfReaction(org.sbml.jsbml.Reaction reaction){     
        if(reaction.isSetCompartment())            return reaction.getCompartment();
        else if (reaction.isSetListOfProducts())   return reaction.getProduct(0).getSpeciesInstance().getCompartment();
        else if(reaction.isSetListOfReactants())   return reaction.getReactant(0).getSpeciesInstance().getCompartment();
        else if(reaction.isSetListOfModifiers())   return reaction.getModifier(0).getSpeciesInstance().getCompartment();
        return null;         
    }
    
    private boolean insertReaction(Model model, org.sbml.jsbml.Reaction reaction){
        model.addReaction(reaction);
        for(org.sbml.jsbml.Parameter param: originalModel.getListOfParameters())
            if(reaction.getKineticLaw().getMathMLString().contains(param.getId())&& !containsParameter(model, param.getId())){ 
                model.addParameter(param);
            if(param.isSetUnits()&&!containsUnitDefinition(model,param.getUnits())) {System.out.println("param: "+param.getUnits()); model.addUnitDefinition(param.getUnitsInstance());}
            }
        for(org.sbml.jsbml.LocalParameter lParam: reaction.getKineticLaw().getListOfLocalParameters())
            if(lParam.isSetUnits()&&!containsUnitDefinition(model, lParam.getUnits())) {System.out.println("lParam: "+lParam.getUnits()); model.addUnitDefinition(lParam.getUnitsInstance());}
        return true;
    }
    
     private boolean containsCompartment(Model model, String compartment){     
        for(org.sbml.jsbml.Compartment ele: model.getListOfCompartments())
            if(ele.getId().equals(compartment)) return true;
        return false;
    }
    
    private boolean containsParameter(Model model, String unit){
        
        for(org.sbml.jsbml.Parameter ele: model.getListOfParameters())
            if(ele.getId().equals(unit)) return true;
        return false;
    }
    
    private boolean containsUnitDefinition(Model model, String unit){
       
        for(org.sbml.jsbml.UnitDefinition ele: model.getListOfUnitDefinitions()){
            if(ele.getId().equals(unit)|| unit.equals("dimensionless")) return true;
        }
        return false;
    }
    
    private boolean insertSpecies(Model model, org.sbml.jsbml.Species species){
        model.addSpecies(species);
        if(species.isSetUnits()&&!containsUnitDefinition(model,species.getUnits())){System.out.println("species: "+species.getUnits());  model.addUnitDefinition(species.getUnitsInstance());}
        setAll(model,species.getId()) ;
        if(!containsCompartment(model,species.getCompartment())) insertCompartment(model, species.getCompartmentInstance());
        return true;
    }
    
     private boolean insertCompartment(Model model, org.sbml.jsbml.Compartment compartment){
        model.addCompartment(compartment);
        if(compartment.isSetUnits()&&!containsUnitDefinition(model,compartment.getUnits())){ System.out.println("compartment: "+compartment.getUnits()); model.addUnitDefinition(compartment.getUnitsInstance());}
        setAll(model,compartment.getId() );
        return true;
    }
    
    public org.sbml.jsbml.Model gripReaction(String id){
        org.sbml.jsbml.Reaction reaction =  originalModel.getReaction(id);
        return gripReaction(reaction); 
        
    }
     
    public org.sbml.jsbml.Model[] gripReactionList( ){
        int nReact = originalModel.getNumReactions();
        Model[] newModels = new org.sbml.jsbml.Model[nReact];
        int i=0;
         for(org.sbml.jsbml.Reaction compartment: originalModel.getListOfReactions()){
             newModels[i++] = gripReaction(compartment);  
        }
       
       return newModels; 
    }
    
    public org.sbml.jsbml.Model gripReaction( org.sbml.jsbml.Reaction reaction){
        Model newModel = new Model();
        insertReaction(newModel,reaction ); 
        insertCompartment(newModel, originalModel.getCompartment(compartmentOfReaction(reaction)));
        for(org.sbml.jsbml.SpeciesReference reactant: reaction.getListOfReactants())     
            insertSpecies(newModel, reactant.getSpeciesInstance() ); 
          
        for(org.sbml.jsbml.SpeciesReference product: reaction.getListOfProducts()) 
            insertSpecies(newModel, product.getSpeciesInstance() );  
            
        for(org.sbml.jsbml.ModifierSpeciesReference modifier: reaction.getListOfModifiers())
            insertSpecies(newModel, modifier.getSpeciesInstance() ); 
                               
        return newModel;
    }
   
    private org.sbml.jsbml.Model gripSpecies( org.sbml.jsbml.Species species){
        Model newModel = new Model();
        newModel.addSpecies(species);
         if(species.isSetUnits()) newModel.addUnitDefinition(species.getUnitsInstance());
                setAll(newModel,species.getId() );
        return newModel; 
        
    }
    
    private org.sbml.jsbml.Model gripSpecies(String id){
        org.sbml.jsbml.Species species =  originalModel.getSpecies(id);
        return gripSpecies(species); 
        
    }
    
    private boolean setAll(Model model, String variable){
    
        if(originalModel.isSetListOfInitialAssignments())
        {
            org.sbml.jsbml.InitialAssignment iAssign = originalModel.getInitialAssignment(variable);
            if(iAssign!=null)   model.addInitialAssignment(iAssign);
        }
        if(originalModel.isSetListOfRules())
            for (org.sbml.jsbml.Rule rule:  originalModel.getListOfRules()){
                if(rule.isAssignment() && ((org.sbml.jsbml.AssignmentRule)rule).getVariable().equals(variable))
                    model.addRule(rule);
                else if(rule.isRate() && ((org.sbml.jsbml.RateRule)rule).getVariable().equals(variable))
                    model.addRule(rule);  
                //if(rule.isAlgebraic() && ((org.sbml.jsbml.AlgebraicRule)rule))
            }
            for(org.sbml.jsbml.Event event: originalModel.getListOfEvents())
                for(org.sbml.jsbml.EventAssignment eAssign: event.getListOfEventAssignments())
                    if(eAssign.getVariable().equals(variable))
                        if( !containsEventAssignment(model, eAssign))
                        if( !containsEvent(model, event)){
                            org.sbml.jsbml.Event newEvent = event.clone();
                            newEvent.unsetListOfEventAssignments();
                            newEvent.addEventAssignment(eAssign);
                            model.addEvent(event);
                        }else
                            getEvent(model, event).addEventAssignment(eAssign);
        if(originalModel.isSetListOfFunctionDefinitions()){
            org.sbml.jsbml.FunctionDefinition fd = originalModel.getFunctionDefinition(variable);
            if(fd!= null)   model.addFunctionDefinition(fd);
        }
      
          return true;
                 
    }
    
    private boolean containsEventAssignment(Model model, org.sbml.jsbml.EventAssignment eventAssignment){
        for(org.sbml.jsbml.Event event: model.getListOfEvents())
          for(org.sbml.jsbml.EventAssignment eAssign: event.getListOfEventAssignments())
              if (eAssign.equals(eventAssignment)) return true;
        return false;
    }
    
     private org.sbml.jsbml.Event getEvent(Model model, org.sbml.jsbml.Event eventCheck){
        for(org.sbml.jsbml.Event event: model.getListOfEvents())
            if (event.getTrigger().equals(eventCheck.getTrigger())&&event.getDelay() .equals(eventCheck.getDelay())
                    && event.getPriority().equals(eventCheck.getPriority())) return event;
            
        return null;
    }
     
     private boolean containsEvent(Model model, org.sbml.jsbml.Event eventCheck){
        for(org.sbml.jsbml.Event event: model.getListOfEvents()){
            if(!event.getTrigger().equals(eventCheck.getTrigger())) return false;
            if(!event.getDelay() .equals(eventCheck.getDelay())) return false;
            if(! event.getPriority().equals(eventCheck.getPriority())) return false;
        }
            
        return true;
    }
}
