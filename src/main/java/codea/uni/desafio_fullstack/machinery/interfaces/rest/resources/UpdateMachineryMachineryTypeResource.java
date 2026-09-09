package codea.uni.desafio_fullstack.machinery.interfaces.rest.resources;

public record UpdateMachineryMachineryTypeResource(String code, Integer machineryTypeId) {
    public UpdateMachineryMachineryTypeResource {
        if (machineryTypeId == null) {
            throw new IllegalArgumentException("MachineryTypeId cannot be null");
        }
        if(code==null || code.length()==0){
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
    }
}
